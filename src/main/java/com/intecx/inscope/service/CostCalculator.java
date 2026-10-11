package com.intecx.inscope.service;

import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CostCalculator {

    private static final BigDecimal MIN_HOURLY_RATE = new BigDecimal("0.01");
    private static final BigDecimal MAX_HOURLY_RATE = new BigDecimal("9999999999.99");
    private static final int MAX_HOURLY_RATE_SCALE = 2;

    public BigDecimal calculate(BigDecimal hours, BigDecimal hourlyRate) {
        if (hours == null) {
            throw new BadRequestException("Las horas estimadas son obligatorias");
        }
        if (hours.signum() < 0) {
            throw new BadRequestException("Las horas estimadas no pueden ser negativas");
        }
        if (hourlyRate == null) {
            throw new BadRequestException("El costo estándar (CER) por hora es obligatorio");
        }
        if (hourlyRate.compareTo(MIN_HOURLY_RATE) < 0) {
            throw new BadRequestException("El costo estándar (CER) por hora debe ser al menos 0.01");
        }
        if (hourlyRate.compareTo(MAX_HOURLY_RATE) > 0) {
            throw new BadRequestException("El costo estándar (CER) no puede exceder 9999999999.99");
        }
        if (hourlyRate.scale() > MAX_HOURLY_RATE_SCALE) {
            throw new BadRequestException("El costo estándar (CER) debe tener como máximo 2 decimales");
        }
        return hours.multiply(hourlyRate);
    }
}
