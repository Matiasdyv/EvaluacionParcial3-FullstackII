package cl.duoc.saludtotal.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El RUT o correo es obligatorio") String identificador,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {}
