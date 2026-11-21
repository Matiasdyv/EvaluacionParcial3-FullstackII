package cl.duoc.saludtotal.dto;

import cl.duoc.saludtotal.model.Cita;
import cl.duoc.saludtotal.model.EstadoCita;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaResponse(
        Long id, Long pacienteId, String paciente,
        Long medicoId, String medico, String especialidad,
        LocalDate fecha, LocalTime hora, String observaciones, EstadoCita estado
) {
    public static CitaResponse de(Cita c) {
        return new CitaResponse(
                c.getId(), c.getPaciente().getId(), c.getPaciente().getNombre(),
                c.getMedico().getId(), c.getMedico().getNombre(), c.getMedico().getEspecialidad().getNombre(),
                c.getFecha(), c.getHora(), c.getObservaciones(), c.getEstado());
    }
}
