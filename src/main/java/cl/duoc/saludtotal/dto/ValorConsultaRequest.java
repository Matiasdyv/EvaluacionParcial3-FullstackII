package cl.duoc.saludtotal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ValorConsultaRequest(
        @NotNull(message = "El valor es obligatorio") @Min(value = 0, message = "El valor no puede ser negativo") Integer valorConsulta) {}
