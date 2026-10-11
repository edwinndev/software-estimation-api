package com.intecx.inscope.service;

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
    void preservesExactFractionalCost() {
        BigDecimal cost = calculator.calculate(new BigDecimal("1.125"), new BigDecimal("85.50"));

        assertEquals(0, new BigDecimal("96.18750").compareTo(cost));
    }

    @Test
    void allowsZeroHours() {
        BigDecimal cost = calculator.calculate(BigDecimal.ZERO, new BigDecimal("85.50"));

        assertEquals(0, BigDecimal.ZERO.compareTo(cost));
    }

    @Test
    void rejectsNullHours() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(null, new BigDecimal("85.50")));
    }

    @Test
    void rejectsNegativeHours() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(new BigDecimal("-0.01"), new BigDecimal("85.50")));
    }

    @Test
    void rejectsNullHourlyRate() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, null));
    }

    @Test
    void rejectsZeroHourlyRate() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, BigDecimal.ZERO));
    }

    @Test
    void rejectsNegativeHourlyRate() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, new BigDecimal("-1")));
    }

    @Test
    void rejectsHourlyRateBelowMinimum() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, new BigDecimal("0.001")));
    }

    @Test
    void allowsMinimumHourlyRate() {
        BigDecimal cost = calculator.calculate(BigDecimal.ONE, new BigDecimal("0.01"));

        assertEquals(0, new BigDecimal("0.01").compareTo(cost));
    }

    @Test
    void allowsMaximumHourlyRate() {
        BigDecimal cost = calculator.calculate(BigDecimal.ONE, new BigDecimal("9999999999.99"));

        assertEquals(0, new BigDecimal("9999999999.99").compareTo(cost));
    }

    @Test
    void rejectsHourlyRateAboveMaximum() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, new BigDecimal("10000000000.00")));
    }

    @Test
    void rejectsHourlyRateWithMoreThanTwoDecimals() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, new BigDecimal("85.501")));
    }

    @Test
    void rejectsHourlyRateWithExcessTrailingDecimalZeros() {
        assertThrows(BadRequestException.class,
                () -> calculator.calculate(BigDecimal.ONE, new BigDecimal("85.500")));
    }

    @Test
    void calculatesConsecutiveCallsIndependently() {
        BigDecimal firstCost = calculator.calculate(new BigDecimal("8"), new BigDecimal("85.50"));
        BigDecimal secondCost = calculator.calculate(new BigDecimal("2.5"), new BigDecimal("40.00"));

        assertEquals(0, new BigDecimal("684.00").compareTo(firstCost));
        assertEquals(0, new BigDecimal("100.00").compareTo(secondCost));
    }
}
