package cl.duoc.saludtotal.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RutValidatorTest {

    private final RutValidator validador = new RutValidator();

    @Test
    void aceptaRutConDigitoVerificadorCorrecto() {
        assertTrue(validador.isValid("12345678-5", null));
        assertTrue(validador.isValid("11111111-1", null));
        assertTrue(validador.isValid("7654321-6", null));
    }

    @Test
    void aceptaDigitoVerificadorKEnMayusculaOMinuscula() {
        assertTrue(validador.isValid("10000013-K", null));
        assertTrue(validador.isValid("10000013-k", null));
    }

    @Test
    void aceptaDigitoVerificadorCero() {
        assertTrue(validador.isValid("10000004-0", null));
    }

    @Test
    void rechazaDigitoVerificadorIncorrecto() {
        assertFalse(validador.isValid("12345678-9", null));
    }

    @Test
    void rechazaFormatoInvalido() {
        assertFalse(validador.isValid("abc", null));
        assertFalse(validador.isValid("12345678", null));
        assertFalse(validador.isValid("12.345.678-5", null));
    }

    @Test
    void valorVacioLoResuelveNotBlank() {
        assertTrue(validador.isValid(null, null));
        assertTrue(validador.isValid("  ", null));
    }
}
