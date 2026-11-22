package cl.duoc.saludtotal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidator implements ConstraintValidator<RutValido, String> {

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context) {
        if (rut == null || rut.isBlank()) return true;
        if (!rut.matches("^\\d{7,8}-[\\dkK]$")) return false;

        String cuerpo = rut.substring(0, rut.indexOf('-'));
        char dvIngresado = Character.toUpperCase(rut.charAt(rut.length() - 1));
        return calcularDv(cuerpo) == dvIngresado;
    }

    static char calcularDv(String cuerpo) {
        int suma = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) ('0' + resto);
    }
}
