package com.intecx.inscope.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TechnicalProfileValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createRequestShouldPassWhenAllFieldsAreValid() {
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Valid Profile")
                .email("valid.profile@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("75.00"))
                .currency(CurrencyCode.PEN)
                .build();

        Set<ConstraintViolation<CreateTechnicalProfileRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-1.00", "-0.01"})
    void createRequestShouldFailWhenHourlyRateIsZeroOrNegative(String invalidRate) {
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Invalid Rate Profile")
                .email("invalid.rate@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .hourlyRate(new BigDecimal(invalidRate))
                .build();

        Set<ConstraintViolation<CreateTechnicalProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hourlyRate")));
    }

    @Test
    void createRequestShouldFailWhenHourlyRateHasMoreThanTwoDecimals() {
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Precision Test")
                .email("precision@example.com")
                .role(TechnicalRole.QA)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("45.123"))
                .build();

        Set<ConstraintViolation<CreateTechnicalProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hourlyRate")));
    }

    @Test
    void createRequestShouldFailWhenEmailIsInvalid() {
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Invalid Email")
                .email("invalid-email-format")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .hourlyRate(new BigDecimal("50.00"))
                .build();

        Set<ConstraintViolation<CreateTechnicalProfileRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void updateCerRequestShouldFailWhenHourlyRateIsZeroOrNegative() {
        UpdateCerRequest request = UpdateCerRequest.builder()
                .hourlyRate(BigDecimal.ZERO)
                .currency(CurrencyCode.PEN)
                .build();

        Set<ConstraintViolation<UpdateCerRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("hourlyRate")));
    }

}
