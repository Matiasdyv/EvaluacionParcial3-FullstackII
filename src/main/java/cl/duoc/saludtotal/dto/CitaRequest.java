package cl.duoc.saludtotal.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record CitaRequest(
        @NotNull(message = "El médico es obligatorio") Long medicoId,
        @NotNull(message = "La fecha es obligatoria") @FutureOrPresent(message = "La fecha no puede ser pasada") LocalDate fecha,
        @NotNull(message = "La hora es obligatoria") LocalTime hora,
        @Size(max = 500, message = "Máximo 500 caracteres") String observaciones
) {}
