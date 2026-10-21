package com.intecx.inscope.service.support;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class CostAggregator {

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");

    public CostAmountResponse subtotal(Collection<CostAmountResponse> amounts) {
        if (amounts == null || amounts.isEmpty()) {
            throw new BadRequestException("Se requiere al menos un importe para calcular el subtotal");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        String currency = null;
        for (CostAmountResponse amount : amounts) {
            validate(amount);
            if (currency == null) {
                currency = amount.currency();
            } else if (!currency.equals(amount.currency())) {
                throw new BadRequestException("No se pueden sumar importes de monedas diferentes");
            }
            subtotal = subtotal.add(amount.amount());
        }
        return new CostAmountResponse(subtotal, currency);
    }

    private void validate(CostAmountResponse amount) {
        if (amount == null) {
            throw new BadRequestException("Los importes no pueden contener elementos nulos");
        }
        if (amount.amount() == null) {
            throw new BadRequestException("El importe es obligatorio");
        }
        if (amount.amount().signum() < 0) {
            throw new BadRequestException("El importe no puede ser negativo");
        }
        if (amount.currency() == null || !CURRENCY_PATTERN.matcher(amount.currency()).matches()) {
            throw new BadRequestException("La moneda es obligatoria y debe contener tres letras mayúsculas");
        }
    }
}
