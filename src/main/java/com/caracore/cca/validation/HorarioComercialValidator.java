package com.caracore.cca.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class HorarioComercialValidator implements ConstraintValidator<HorarioComercial, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull deve ser usada para obrigatoriedade
        int hour = value.getHour();
        int minute = value.getMinute();
        // Horário comercial: 08:00-12:00 e 13:00-18:00
        boolean manha = (hour >= 8 && hour < 12);
        boolean tarde = (hour >= 13 && hour < 18) || (hour == 18 && minute == 0);
        return manha || tarde;
    }
}
