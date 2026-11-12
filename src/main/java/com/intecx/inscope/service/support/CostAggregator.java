package com.intecx.inscope.service.support;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class CostAggregator {

    public CostAmountResponse subtotal(Collection<CostAmountResponse> amounts) {
        if (amounts == null || amounts.isEmpty()) {
            throw new BadRequestException("Se requiere al menos un importe para calcular el subtotal");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        CurrencyCode currency = null;
        for (CostAmountResponse amount : amounts) {
            validate(amount);
            if (currency == null) {
                currency = amount.currency();
            } else if (currency != amount.currency()) {
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
        if (amount.currency() == null) {
            throw new BadRequestException("La moneda es obligatoria");
        }
    }
}
