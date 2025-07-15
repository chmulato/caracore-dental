package com.caracore.cca.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = HorarioComercialValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface HorarioComercial {
    String message() default "Horário deve estar dentro do expediente (08:00-12:00, 13:00-18:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
