package com.intecx.inscope.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CostAggregatorTest {

    private final CostAggregator aggregator = new CostAggregator();

    @Test
    void sumsSeveralAmounts() {
        assertSubtotal("15.75", "PEN", List.of(amount("10.25", "PEN"), amount("2", "PEN"), amount("3.50", "PEN")));
    }

    @Test
    void preservesExactDecimals() {
        assertSubtotal("0.123456790", "PEN", List.of(amount("0.123456789", "PEN"), amount("0.000000001", "PEN")));
    }

    @Test
    void acceptsSingleAmount() {
        assertSubtotal("85.50", "USD", List.of(amount("85.50", "USD")));
    }

    @Test
    void acceptsZeroAmount() {
        assertSubtotal("0", "PEN", List.of(amount("0", "PEN")));
    }

    @Test
    void rejectsNullCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(null));
    }

    @Test
    void rejectsEmptyCollection() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of()));
    }

    @Test
    void rejectsNullElement() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(Arrays.asList(amount("1", "PEN"), null)));
    }

    @Test
    void rejectsNullAmount() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(new CostAmountResponse(null, "PEN"))));
    }

    @Test
    void rejectsNegativeAmount() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(amount("-0.01", "PEN"))));
    }

    @Test
    void rejectsNullCurrency() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(amount("1", null))));
    }

    @Test
    void rejectsEmptyAndBlankCurrencies() {
        for (String currency : List.of("", " ", "\t", "\n")) {
            assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(amount("1", currency))));
        }
    }

    @Test
    void rejectsInvalidCurrencyFormats() {
        for (String currency : List.of("pen", "Pen", "PE", "PENN", "P3N", " PEN ", "PEN\n")) {
            assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(amount("1", currency))));
        }
    }

    @Test
    void rejectsDifferentCurrencies() {
        assertThrows(BadRequestException.class, () -> aggregator.subtotal(List.of(amount("1", "PEN"), amount("2", "USD"))));
    }

    @Test
    void sumsRepeatedCurrency() {
        assertSubtotal("6", "USD", List.of(amount("1", "USD"), amount("2", "USD"), amount("3", "USD")));
    }

    @Test
    void doesNotRoundContributionsOrSubtotal() {
        CostAmountResponse result = aggregator.subtotal(List.of(amount("0.005", "PEN"), amount("0.005", "PEN")));
        assertEquals(new BigDecimal("0.010"), result.amount());
        assertEquals("PEN", result.currency());
    }

    @Test
    void doesNotRequireIsoCurrencyMembership() {
        assertSubtotal("1", "ZZZ", List.of(amount("1", "ZZZ")));
    }

    @Test
    void keepsConsecutiveCallsIndependent() {
        assertSubtotal("1", "PEN", List.of(amount("1", "PEN")));
        assertSubtotal("2", "USD", List.of(amount("2", "USD")));
    }

    private CostAmountResponse amount(String value, String currency) {
        return new CostAmountResponse(new BigDecimal(value), currency);
    }

    private void assertSubtotal(String expected, String currency, List<CostAmountResponse> amounts) {
        CostAmountResponse result = aggregator.subtotal(amounts);
        assertEquals(0, new BigDecimal(expected).compareTo(result.amount()));
        assertEquals(currency, result.currency());
    }
}
