package cl.duoc.saludtotal.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegistroRequest(
        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "RUT inválido (ej: 12345678-9)") String rut,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 100) String email,
        @Pattern(regexp = "^(\\+?56)?\\s?9\\d{8}$", message = "Teléfono inválido") String telefono,
        @Past(message = "La fecha de nacimiento debe ser pasada") LocalDate fechaNacimiento,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 60, message = "La contraseña debe tener entre 6 y 60 caracteres") String password
) {}
