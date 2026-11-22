package cl.duoc.saludtotal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RutValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface RutValido {
    String message() default "RUT inválido: el dígito verificador no corresponde";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
