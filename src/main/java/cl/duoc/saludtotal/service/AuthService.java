package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.AuthResponse;
import cl.duoc.saludtotal.dto.LoginRequest;
import cl.duoc.saludtotal.dto.RegistroRequest;
import cl.duoc.saludtotal.exception.ConflictoException;
import cl.duoc.saludtotal.exception.CredencialesInvalidasException;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.RolRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import cl.duoc.saludtotal.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder, JwtUtil jwt) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse registrar(RegistroRequest req) {
        if (usuarios.existsByRut(req.rut())) throw new ConflictoException("Ya existe un usuario con ese RUT");
        if (usuarios.existsByEmail(req.email())) throw new ConflictoException("Ya existe un usuario con ese correo");

        Usuario u = new Usuario();
        u.setRut(req.rut());
        u.setNombre(req.nombre());
        u.setEmail(req.email());
        u.setTelefono(req.telefono());
        u.setFechaNacimiento(req.fechaNacimiento());
        u.setPassword(encoder.encode(req.password()));
        u.setRol(roles.findByNombre("PACIENTE").orElseThrow());
        u.setActivo(true);
        return respuesta(usuarios.save(u));
    }

    public AuthResponse login(LoginRequest req) {
        String id = req.identificador().trim();
        Usuario u = (id.contains("@") ? usuarios.findByEmail(id) : usuarios.findByRut(id))
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales incorrectas"));

        if (!u.isActivo() || !encoder.matches(req.password(), u.getPassword())) {
            throw new CredencialesInvalidasException("Credenciales incorrectas");
        }
        return respuesta(u);
    }

    private AuthResponse respuesta(Usuario u) {
        String token = jwt.generar(u.getEmail(), u.getRol().getNombre());
        return new AuthResponse(token, u.getId(), u.getRut(), u.getNombre(), u.getEmail(), u.getRol().getNombre());
    }
}
