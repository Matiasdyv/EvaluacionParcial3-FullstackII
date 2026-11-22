package cl.duoc.saludtotal.service;

import cl.duoc.saludtotal.dto.AuthResponse;
import cl.duoc.saludtotal.dto.LoginRequest;
import cl.duoc.saludtotal.dto.RegistroRequest;
import cl.duoc.saludtotal.exception.ConflictoException;
import cl.duoc.saludtotal.exception.CredencialesInvalidasException;
import cl.duoc.saludtotal.model.Rol;
import cl.duoc.saludtotal.model.Usuario;
import cl.duoc.saludtotal.repository.RolRepository;
import cl.duoc.saludtotal.repository.UsuarioRepository;
import cl.duoc.saludtotal.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepository usuarios;
    @Mock RolRepository roles;
    @Mock PasswordEncoder encoder;
    @Mock JwtUtil jwt;
    @InjectMocks AuthService servicio;

    private RegistroRequest registro() {
        return new RegistroRequest("12345678-5", "Ana", "Pérez", "Soto", "ana@mail.com", "912345678",
                LocalDate.of(2000, 5, 10), "123456789", "fonasa", "femenino", "Región Metropolitana",
                "Calle 1", false, "clave123");
    }

    private Usuario usuario(boolean activo) {
        Usuario u = new Usuario();
        u.setId(2L);
        u.setRut("12345678-5");
        u.setNombre("Ana");
        u.setEmail("ana@mail.com");
        u.setPassword("HASH");
        u.setRol(new Rol(2L, "PACIENTE"));
        u.setActivo(activo);
        return u;
    }

    @Test
    void registrarGuardaLaContrasenaCifradaConRolPaciente() {
        when(usuarios.existsByRut("12345678-5")).thenReturn(false);
        when(usuarios.existsByEmail("ana@mail.com")).thenReturn(false);
        when(encoder.encode("clave123")).thenReturn("HASH");
        when(roles.findByNombre("PACIENTE")).thenReturn(Optional.of(new Rol(2L, "PACIENTE")));
        when(usuarios.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        when(jwt.generar("ana@mail.com", "PACIENTE")).thenReturn("TOKEN");

        AuthResponse respuesta = servicio.registrar(registro());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(guardado.capture());
        assertEquals("HASH", guardado.getValue().getPassword());
        assertNotEquals("clave123", guardado.getValue().getPassword());
        assertEquals("PACIENTE", guardado.getValue().getRol().getNombre());
        assertEquals("TOKEN", respuesta.token());
        assertEquals("PACIENTE", respuesta.rol());
    }

    @Test
    void registrarRechazaUnRutRepetido() {
        when(usuarios.existsByRut("12345678-5")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> servicio.registrar(registro()));
        verify(usuarios, never()).save(any());
    }

    @Test
    void registrarRechazaUnCorreoRepetido() {
        when(usuarios.existsByRut("12345678-5")).thenReturn(false);
        when(usuarios.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> servicio.registrar(registro()));
        verify(usuarios, never()).save(any());
    }

    @Test
    void loginConRutDevuelveToken() {
        when(usuarios.findByRut("12345678-5")).thenReturn(Optional.of(usuario(true)));
        when(encoder.matches("clave123", "HASH")).thenReturn(true);
        when(jwt.generar("ana@mail.com", "PACIENTE")).thenReturn("TOKEN");

        AuthResponse respuesta = servicio.login(new LoginRequest("12345678-5", "clave123"));

        assertEquals("TOKEN", respuesta.token());
        assertEquals("Ana", respuesta.nombre());
    }

    @Test
    void loginConCorreoBuscaPorEmail() {
        when(usuarios.findByEmail("ana@mail.com")).thenReturn(Optional.of(usuario(true)));
        when(encoder.matches("clave123", "HASH")).thenReturn(true);
        when(jwt.generar("ana@mail.com", "PACIENTE")).thenReturn("TOKEN");

        AuthResponse respuesta = servicio.login(new LoginRequest("ana@mail.com", "clave123"));

        assertEquals("TOKEN", respuesta.token());
    }

    @Test
    void loginRechazaUnaContrasenaIncorrecta() {
        when(usuarios.findByRut("12345678-5")).thenReturn(Optional.of(usuario(true)));
        when(encoder.matches("mala", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("12345678-5", "mala")));
    }

    @Test
    void loginRechazaUnUsuarioInexistente() {
        when(usuarios.findByRut("99999999-9")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("99999999-9", "clave123")));
    }

    @Test
    void loginRechazaUnUsuarioInactivo() {
        when(usuarios.findByRut("12345678-5")).thenReturn(Optional.of(usuario(false)));

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.login(new LoginRequest("12345678-5", "clave123")));
    }
}
