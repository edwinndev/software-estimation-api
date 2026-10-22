package com.intecx.inscope.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TechnicalProfileMapperTest {

    private TechnicalProfileMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TechnicalProfileMapper();
    }

    @Test
    void toEntityShouldReturnNullWhenRequestIsNull() {
        assertNull(mapper.toEntity(null, UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(value = TechnicalRole.class, names = {"FRONTEND", "BACKEND", "FUNCTIONAL_ANALYST", "QA"})
    void toEntityShouldMapProfileRegistrationFieldsAndDefaultValues(TechnicalRole role) {
        UUID actorId = UUID.randomUUID();
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Jane Developer")
                .email("  JANE.DEV@EXAMPLE.COM  ")
                .role(role)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("75.50"))
                .build();

        TechnicalProfile entity = mapper.toEntity(request, actorId);

        assertNotNull(entity);
        assertEquals("Jane Developer", entity.getName());
        assertEquals("jane.dev@example.com", entity.getEmail());
        assertEquals(role, entity.getRole());
        assertEquals(ExperienceLevel.SENIOR, entity.getExperienceLevel());
        assertEquals(new BigDecimal("75.50"), entity.getHourlyRate());
        assertEquals(CurrencyCode.PEN, entity.getCurrency());
        assertTrue(entity.isActive());
        assertEquals(actorId, entity.getCreatedBy());
    }

    @Test
    void toEntityShouldUseCustomCurrencyAndActiveStatusWhenProvided() {
        UUID actorId = UUID.randomUUID();
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Alex Smith")
                .email("alex@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("60.00"))
                .currency(CurrencyCode.PEN)
                .isActive(false)
                .build();

        TechnicalProfile entity = mapper.toEntity(request, actorId);

        assertNotNull(entity);
        assertEquals(CurrencyCode.PEN, entity.getCurrency());
        assertEquals(false, entity.isActive());
    }

    @Test
    void toResponseShouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponseShouldMapEntityFieldsAndCerCorrectly() {
        UUID profileId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        TechnicalProfile entity = TechnicalProfile.builder()
                .id(profileId)
                .name("Valeria Lopez")
                .email("valeria@example.com")
                .role(TechnicalRole.FUNCTIONAL_ANALYST)
                .experienceLevel(ExperienceLevel.LEAD)
                .hourlyRate(new BigDecimal("110.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        TechnicalProfileResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(profileId, response.id());
        assertEquals("Valeria Lopez", response.name());
        assertEquals("valeria@example.com", response.email());
        assertEquals(TechnicalRole.FUNCTIONAL_ANALYST, response.role());
        assertEquals(ExperienceLevel.LEAD, response.experienceLevel());
        assertEquals(new BigDecimal("110.00"), response.hourlyRate());
        assertEquals(CurrencyCode.PEN, response.currency());
        assertTrue(response.isActive());
        assertEquals(now, response.createdAt());
        assertEquals(now.plusHours(1), response.updatedAt());
    }
}
