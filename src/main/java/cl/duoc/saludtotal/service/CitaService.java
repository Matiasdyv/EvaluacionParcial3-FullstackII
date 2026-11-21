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

    public Cita agendar(CitaRequest req) {
        Usuario paciente = usuarios.findById(req.pacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado: " + req.pacienteId()));
        Medico medico = medicos.findById(req.medicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado: " + req.medicoId()));

        // Regla: un médico no puede tener 2 citas activas a la misma fecha y hora
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

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citas.findByPacienteIdOrderByFechaDescHoraDesc(pacienteId);
    }

    public Cita cambiarEstado(Long id, EstadoCita estado) {
        Cita c = citas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
        c.setEstado(estado);
        return citas.save(c);
    }
}
