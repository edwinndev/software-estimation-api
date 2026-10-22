package com.intecx.inscope.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CostAggregatorTest {

    private final CostAggregator aggregator = new CostAggregator();

    @Test
    void sumsSeveralAmounts() {
        CostAmountResponse result = aggregator.subtotal(List.of(
                amount("10.25", CurrencyCode.PEN),
                amount("2", CurrencyCode.PEN),
                amount("3.50", CurrencyCode.PEN)
        ));

        assertEquals(0, new BigDecimal("15.75").compareTo(result.amount()));
        assertEquals(CurrencyCode.PEN, result.currency());
    }

    @Test
    void rejectsEmptyCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of()));
    }

    private CostAmountResponse amount(String value, CurrencyCode currency) {
        return new CostAmountResponse(new BigDecimal(value), currency);
    }
}
