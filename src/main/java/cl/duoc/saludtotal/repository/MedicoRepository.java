package cl.duoc.saludtotal.repository;

import cl.duoc.saludtotal.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByActivoTrue();
    List<Medico> findByEspecialidadIdAndActivoTrue(Long especialidadId);
}
