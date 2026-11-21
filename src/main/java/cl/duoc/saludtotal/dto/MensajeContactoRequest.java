package cl.duoc.saludtotal.dto;

import jakarta.validation.constraints.*;

public record MensajeContactoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 100) String email,
        @Size(max = 20) String telefono,
        @Size(max = 150) String motivo,
        @NotBlank(message = "El mensaje es obligatorio") @Size(max = 1000, message = "Máximo 1000 caracteres") String mensaje
) {}
