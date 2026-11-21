package cl.duoc.saludtotal.dto;

import cl.duoc.saludtotal.model.EstadoCita;
import jakarta.validation.constraints.NotNull;

public record EstadoCitaRequest(@NotNull(message = "El estado es obligatorio") EstadoCita estado) {}
