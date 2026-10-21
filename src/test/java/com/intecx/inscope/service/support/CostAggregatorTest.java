package com.intecx.inscope.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CostAggregatorTest {

    private final CostAggregator aggregator = new CostAggregator();

    @Test
    void sumsSeveralAmounts() {
        CostAmountResponse result = aggregator.subtotal(List.of(
                amount("10.25", "PEN"),
                amount("2", "PEN"),
                amount("3.50", "PEN")
        ));

        assertEquals(0, new BigDecimal("15.75").compareTo(result.amount()));
        assertEquals("PEN", result.currency());
    }

    @Test
    void rejectsEmptyCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of()));
    }

    @Test
    void rejectsDifferentCurrencies() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(
                amount("1", "PEN"),
                amount("1", "USD")
        )));
    }

    private CostAmountResponse amount(String value, String currency) {
        return new CostAmountResponse(new BigDecimal(value), currency);
    }
}
