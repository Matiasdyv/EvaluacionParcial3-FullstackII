package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.exception.ConflictoException;
import cl.duoc.saludtotal.exception.RecursoNoEncontradoException;
import cl.duoc.saludtotal.exception.ReglaNegocioException;
import cl.duoc.saludtotal.model.Cita;
import cl.duoc.saludtotal.model.EstadoCita;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.CitaRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citas;
    private final UsuarioRepository usuarios;
    private final MedicoRepository medicos;

    public CitaService(CitaRepository citas, UsuarioRepository usuarios, MedicoRepository medicos) {
        this.citas = citas;
        this.usuarios = usuarios;
        this.medicos = medicos;
    }

    public Cita agendar(String emailPaciente, CitaRequest req) {
        Usuario paciente = usuarios.findByEmail(emailPaciente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Medico medico = medicos.findById(req.medicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado: " + req.medicoId()));

        //la hora debe estar dentro del horario de atención del médico
        if (medico.getHoraInicio() != null && medico.getHoraFin() != null) {
            LocalTime inicio = LocalTime.parse(medico.getHoraInicio());
            LocalTime fin = LocalTime.parse(medico.getHoraFin());
            if (req.hora().isBefore(inicio) || !req.hora().isBefore(fin)) {
                throw new ReglaNegocioException(
                        medico.getNombre() + " atiende de " + medico.getHoraInicio() + " a " + medico.getHoraFin() + ".");
            }
        }

        //un médico no puede tener 2 citas activas a la misma fecha y hora
        if (citas.existsByMedicoIdAndFechaAndHoraAndEstadoNot(
                medico.getId(), req.fecha(), req.hora(), EstadoCita.CANCELADA)) {
            throw new ConflictoException("Ese horario ya está reservado.");
        }

        Cita c = new Cita();
        c.setPaciente(paciente);
        c.setMedico(medico);
        c.setFecha(req.fecha());
        c.setHora(req.hora());
        c.setObservaciones(req.observaciones());
        return citas.save(c);
    }

    public List<Cita> listarTodas() {
        return citas.findAll();
    }

    public List<Cita> listarDelPaciente(String emailPaciente) {
        Usuario paciente = usuarios.findByEmail(emailPaciente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return citas.findByPacienteIdOrderByFechaDescHoraDesc(paciente.getId());
    }

    public Cita cambiarEstado(Long id, EstadoCita estado) {
        Cita c = buscar(id);
        c.setEstado(estado);
        return citas.save(c);
    }

    /** El paciente confirma una cita suya que está SOLICITADA. */
    public Cita confirmarPropia(Long id, String emailPaciente) {
        Cita c = buscarPropia(id, emailPaciente);
        if (c.getEstado() != EstadoCita.SOLICITADA) {
            throw new ReglaNegocioException("Solo se pueden confirmar citas solicitadas.");
        }
        c.setEstado(EstadoCita.CONFIRMADA);
        return citas.save(c);
    }

    /** El paciente cancela una cita suya que está SOLICITADA o CONFIRMADA. */
    public Cita cancelarPropia(Long id, String emailPaciente) {
        Cita c = buscarPropia(id, emailPaciente);
        if (c.getEstado() != EstadoCita.SOLICITADA && c.getEstado() != EstadoCita.CONFIRMADA) {
            throw new ReglaNegocioException("Esta cita ya no se puede cancelar.");
        }
        c.setEstado(EstadoCita.CANCELADA);
        return citas.save(c);
    }

    private Cita buscar(Long id) {
        return citas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }

    /** Si la cita es de otro paciente, responde "no encontrada" para no revelar que existe. */
    private Cita buscarPropia(Long id, String emailPaciente) {
        Cita c = buscar(id);
        if (!c.getPaciente().getEmail().equals(emailPaciente)) {
            throw new RecursoNoEncontradoException("Cita no encontrada: " + id);
        }
        return c;
    }
}
