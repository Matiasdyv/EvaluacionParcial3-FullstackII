package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.CitaRequest;
import cl.duoc.saludtotal.exception.ConflictoException;
import cl.duoc.saludtotal.exception.RecursoNoEncontradoException;
import cl.duoc.saludtotal.model.Cita;
import cl.duoc.saludtotal.model.EstadoCita;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.CitaRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

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

        if (citas.existsByMedicoIdAndFechaAndHoraAndEstadoNot(
                medico.getId(), req.fecha(), req.hora(), EstadoCita.CANCELADA)) {
            throw new ConflictoException("El médico ya tiene una cita en esa fecha y hora");
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

    /** Uso del administrador. */
    public Cita cambiarEstado(Long id, EstadoCita estado) {
        Cita c = buscar(id);
        c.setEstado(estado);
        return citas.save(c);
    }

    public Cita cancelarPropia(Long id, String emailPaciente) {
        Cita c = buscar(id);
        if (!c.getPaciente().getEmail().equals(emailPaciente)) {
            throw new RecursoNoEncontradoException("Cita no encontrada: " + id);
        }
        c.setEstado(EstadoCita.CANCELADA);
        return citas.save(c);
    }

    private Cita buscar(Long id) {
        return citas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }
}
