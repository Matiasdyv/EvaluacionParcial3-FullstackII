package cl.duoc.saludtotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.duoc.saludtotal.dto.AuthResponse;
import cl.duoc.saludtotal.dto.LoginRequest;
import cl.duoc.saludtotal.dto.RegistroRequest;
import cl.duoc.saludtotal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService servicio;

    public AuthController(AuthService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Registrar un paciente nuevo")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return servicio.registrar(req);
    }

    @Operation(summary = "Iniciar sesión con RUT o correo y obtener el token JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return servicio.login(req);
    }
}
