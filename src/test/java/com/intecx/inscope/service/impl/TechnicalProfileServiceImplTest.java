package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TechnicalProfileServiceImplTest {

    @Mock
    private TechnicalProfileRepository technicalProfileRepository;

    private TechnicalProfileMapper technicalProfileMapper;
    private TechnicalProfileServiceImpl technicalProfileService;

    @BeforeEach
    void setUp() {
        technicalProfileMapper = new TechnicalProfileMapper();
        technicalProfileService = new TechnicalProfileServiceImpl(technicalProfileRepository, technicalProfileMapper);
    }

    @ParameterizedTest
    @EnumSource(value = TechnicalRole.class, names = {"FRONTEND", "BACKEND", "FUNCTIONAL_ANALYST", "QA"})
    void createShouldRegisterTechnicalProfileWithInitialCer(TechnicalRole role) {
        UUID actorId = UUID.randomUUID();
        UUID generatedId = UUID.randomUUID();
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Alex Tester")
                .email("alex.tester@example.com")
                .role(role)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("80.00"))
                .currency("PEN")
                .build();

        when(technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull("alex.tester@example.com"))
                .thenReturn(false);
        when(technicalProfileRepository.save(any(TechnicalProfile.class)))
                .thenAnswer(invocation -> {
                    TechnicalProfile entity = invocation.getArgument(0);
                    entity.setId(generatedId);
                    return entity;
                });

        TechnicalProfileResponse response = technicalProfileService.create(request, actorId);

        assertNotNull(response);
        assertEquals(generatedId, response.id());
        assertEquals("Alex Tester", response.name());
        assertEquals("alex.tester@example.com", response.email());
        assertEquals(role, response.role());
        assertEquals(ExperienceLevel.MID, response.experienceLevel());
        assertEquals(new BigDecimal("80.00"), response.hourlyRate());
        assertEquals("PEN", response.currency());
        assertTrue(response.isActive());

        ArgumentCaptor<TechnicalProfile> captor = ArgumentCaptor.forClass(TechnicalProfile.class);
        verify(technicalProfileRepository).save(captor.capture());
        TechnicalProfile saved = captor.getValue();
        assertEquals(actorId, saved.getCreatedBy());
        assertEquals(role, saved.getRole());
        assertEquals(new BigDecimal("80.00"), saved.getHourlyRate());
    }
}
