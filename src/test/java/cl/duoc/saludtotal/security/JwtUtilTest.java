package cl.duoc.saludtotal.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRETO = "secreto-de-prueba-con-mas-de-32-caracteres-0123456789";

    @Test
    void generaUnTokenQueSePuedeLeer() {
        JwtUtil jwt = new JwtUtil(SECRETO, 60_000);

        Claims claims = jwt.leer(jwt.generar("paciente@mail.com", "PACIENTE"));

        assertNotNull(claims);
        assertEquals("paciente@mail.com", claims.getSubject());
        assertEquals("PACIENTE", claims.get("rol", String.class));
    }

        @Test
    void rechazaUnTokenAlterado() {
        JwtUtil jwt = new JwtUtil(SECRETO, 60_000);
        String[] partes = jwt.generar("paciente@mail.com", "PACIENTE").split("\\.");

        String contenidoFalso = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"paciente@mail.com\",\"rol\":\"ADMIN\"}".getBytes(StandardCharsets.UTF_8));
        String tokenAlterado = partes[0] + "." + contenidoFalso + "." + partes[2];

        assertNull(jwt.leer(tokenAlterado));
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() {
        JwtUtil emisor = new JwtUtil("otra-clave-distinta-con-mas-de-32-caracteres-9876543210", 60_000);
        JwtUtil receptor = new JwtUtil(SECRETO, 60_000);

        assertNull(receptor.leer(emisor.generar("paciente@mail.com", "ADMIN")));
    }

    @Test
    void rechazaUnTokenVencido() {
        JwtUtil jwt = new JwtUtil(SECRETO, -1_000);

        assertNull(jwt.leer(jwt.generar("paciente@mail.com", "PACIENTE")));
    }

    @Test
    void rechazaTextoQueNoEsUnToken() {
        JwtUtil jwt = new JwtUtil(SECRETO, 60_000);

        assertNull(jwt.leer("esto-no-es-un-token"));
    }
}
