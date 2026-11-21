package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.exception.RecursoNoEncontradoException;
import cl.duoc.saludtotal.model.Especialidad;
import cl.duoc.saludtotal.model.Medico;
import cl.duoc.saludtotal.repository.EspecialidadRepository;
import cl.duoc.saludtotal.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {

    private final EspecialidadRepository especialidades;
    private final MedicoRepository medicos;

    public CatalogoService(EspecialidadRepository especialidades, MedicoRepository medicos) {
        this.especialidades = especialidades;
        this.medicos = medicos;
    }

    public List<Especialidad> listarEspecialidades() {
        return especialidades.findAll();
    }

    public List<Medico> listarMedicos(Long especialidadId) {
        return especialidadId == null
                ? medicos.findByActivoTrue()
                : medicos.findByEspecialidadIdAndActivoTrue(especialidadId);
    }

    public Medico obtenerMedico(Long id) {
        return medicos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado: " + id));
    }

    public Medico actualizarValorConsulta(Long id, Integer valor) {
        Medico m = obtenerMedico(id);
        m.setValorConsulta(valor);
        return medicos.save(m);
    }
}
