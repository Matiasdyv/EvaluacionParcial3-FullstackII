package cl.duoc.saludtotal.controller;

import cl.duoc.saludtotal.dto.AuthResponse;
import cl.duoc.saludtotal.dto.LoginRequest;
import cl.duoc.saludtotal.dto.RegistroRequest;
import cl.duoc.saludtotal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService servicio;

    public AuthController(AuthService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return servicio.registrar(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return servicio.login(req);
    }
}
