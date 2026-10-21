package com.intecx.inscope.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.TechnicalProfileService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TechnicalProfileControllerTest {

    @Mock
    private TechnicalProfileService technicalProfileService;

    private TechnicalProfileController controller;

    @BeforeEach
    void setUp() {
        controller = new TechnicalProfileController(technicalProfileService);
    }

    @Test
    void createShouldReturnCreatedStatusAndRegisteredProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(actorId);

        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Carlos Developer")
                .email("carlos@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("90.00"))
                .currency("PEN")
                .build();

        TechnicalProfileResponse mockResponse = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Carlos Developer")
                .email("carlos@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("90.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        when(technicalProfileService.create(request, actorId)).thenReturn(mockResponse);

        ResponseEntity<TechnicalProfileResponse> response = controller.create(request, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profileId, response.getBody().id());
        assertEquals(TechnicalRole.BACKEND, response.getBody().role());
        assertEquals(new BigDecimal("90.00"), response.getBody().hourlyRate());
        verify(technicalProfileService).create(request, actorId);
    }
}
