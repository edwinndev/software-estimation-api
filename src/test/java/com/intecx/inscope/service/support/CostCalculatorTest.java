package com.intecx.inscope.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intecx.inscope.exception.BadRequestException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CostCalculatorTest {

    private final CostCalculator calculator = new CostCalculator();

    @Test
    void calculatesCostNumerically() {
        BigDecimal cost = calculator.calculate(new BigDecimal("8"), new BigDecimal("85.50"));

        assertEquals(0, new BigDecimal("684.00").compareTo(cost));
    }

    @Test
    void allowsZeroHours() {
        BigDecimal cost = calculator.calculate(BigDecimal.ZERO, new BigDecimal("85.50"));

        assertEquals(0, BigDecimal.ZERO.compareTo(cost));
    }

    @Test
    void rejectsNegativeHours() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(new BigDecimal("-0.01"), new BigDecimal("85.50")));
    }
}
