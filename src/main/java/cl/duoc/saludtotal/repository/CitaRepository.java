package cl.duoc.saludtotal.repository;

import cl.duoc.saludtotal.model.Cita;
import cl.duoc.saludtotal.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteIdOrderByFechaDescHoraDesc(Long pacienteId);

    boolean existsByMedicoIdAndFechaAndHoraAndEstadoNot(
            Long medicoId, LocalDate fecha, LocalTime hora, EstadoCita estado);
}
