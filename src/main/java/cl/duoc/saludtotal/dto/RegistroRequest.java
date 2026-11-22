package cl.duoc.saludtotal.dto;

import cl.duoc.saludtotal.validation.RutValido;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegistroRequest(
        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "RUT inválido (ej: 12345678-5)")
        @RutValido String rut,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @Size(max = 100) String apellidoPaterno,
        @Size(max = 100) String apellidoMaterno,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 100) String email,
        @Pattern(regexp = "^(\\+?56)?\\s?9\\d{8}$", message = "Teléfono inválido") String telefono,
        @Past(message = "La fecha de nacimiento debe ser pasada") LocalDate fechaNacimiento,
        @Size(max = 10) String nroDocumento,
        @Size(max = 20) String seguro,
        @Size(max = 20) String genero,
        @Size(max = 100) String region,
        @Size(max = 200) String direccion,
        Boolean ofertas,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 60, message = "La contraseña debe tener entre 6 y 60 caracteres") String password
) {}
