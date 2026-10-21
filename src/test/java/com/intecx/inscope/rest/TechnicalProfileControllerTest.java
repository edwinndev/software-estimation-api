package com.intecx.inscope.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.common.PageMeta;
import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.ToggleProfileStatusRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.request.profile.UpdateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.TechnicalProfileService;
import java.math.BigDecimal;
import java.util.List;
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

    @Test
    void updateCerShouldReturnOkStatusAndUpdatedProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(actorId);

        UpdateCerRequest request = UpdateCerRequest.builder()
                .hourlyRate(new BigDecimal("120.00"))
                .currency("PEN")
                .build();

        TechnicalProfileResponse mockResponse = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Carlos Developer")
                .email("carlos@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("120.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        when(technicalProfileService.updateCer(profileId, request, actorId)).thenReturn(mockResponse);

        ResponseEntity<TechnicalProfileResponse> response = controller.updateCer(profileId, request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("120.00"), response.getBody().hourlyRate());
        verify(technicalProfileService).updateCer(profileId, request, actorId);
    }

    @Test
    void updateShouldReturnOkStatusAndUpdatedProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(actorId);

        UpdateTechnicalProfileRequest request = UpdateTechnicalProfileRequest.builder()
                .name("Carlos New")
                .email("carlos.new@example.com")
                .role(TechnicalRole.FULLSTACK)
                .experienceLevel(ExperienceLevel.LEAD)
                .hourlyRate(new BigDecimal("130.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        TechnicalProfileResponse mockResponse = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Carlos New")
                .email("carlos.new@example.com")
                .role(TechnicalRole.FULLSTACK)
                .experienceLevel(ExperienceLevel.LEAD)
                .hourlyRate(new BigDecimal("130.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        when(technicalProfileService.update(profileId, request, actorId)).thenReturn(mockResponse);

        ResponseEntity<TechnicalProfileResponse> response = controller.update(profileId, request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TechnicalRole.FULLSTACK, response.getBody().role());
        assertEquals(new BigDecimal("130.00"), response.getBody().hourlyRate());
        verify(technicalProfileService).update(profileId, request, actorId);
    }

    @Test
    void getByIdShouldReturnOkStatusAndProfile() {
        UUID profileId = UUID.randomUUID();
        TechnicalProfileResponse mockResponse = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Maria QA")
                .email("maria@example.com")
                .role(TechnicalRole.QA)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("70.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        when(technicalProfileService.getById(profileId)).thenReturn(mockResponse);

        ResponseEntity<TechnicalProfileResponse> response = controller.getById(profileId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profileId, response.getBody().id());
        assertEquals(new BigDecimal("70.00"), response.getBody().hourlyRate());
        verify(technicalProfileService).getById(profileId);
    }

    @Test
    void searchShouldReturnOkStatusAndPaginatedProfiles() {
        UUID profileId = UUID.randomUUID();
        QueryRequest query = new QueryRequest(null, null);

        TechnicalProfileResponse item = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Ana Lead")
                .email("ana@example.com")
                .role(TechnicalRole.TECH_LEAD)
                .experienceLevel(ExperienceLevel.LEAD)
                .hourlyRate(new BigDecimal("140.00"))
                .currency("PEN")
                .isActive(true)
                .build();

        PageMeta meta = PageMeta.of(1L, 0, 20);
        PaginatedResponse<TechnicalProfileResponse> mockPaginated = PaginatedResponse.of("profilesResponse", List.of(item), meta);

        when(technicalProfileService.search(query)).thenReturn(mockPaginated);

        ResponseEntity<PaginatedResponse<TechnicalProfileResponse>> response = controller.search(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(new BigDecimal("140.00"), response.getBody().getItems().getFirst().hourlyRate());
        verify(technicalProfileService).search(query);
    }

    @Test
    void searchShouldReturnOkStatusWhenQueryIsNull() {
        PageMeta meta = PageMeta.of(0L, 0, 20);
        PaginatedResponse<TechnicalProfileResponse> mockPaginated = PaginatedResponse.of("profilesResponse", List.of(), meta);

        when(technicalProfileService.search(null)).thenReturn(mockPaginated);

        ResponseEntity<PaginatedResponse<TechnicalProfileResponse>> response = controller.search(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getItems().isEmpty());
        verify(technicalProfileService).search(null);
    }

    @Test
    void changeStatusShouldReturnOkStatusAndUpdatedProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(actorId);

        ToggleProfileStatusRequest request = ToggleProfileStatusRequest.builder()
                .isActive(false)
                .build();

        TechnicalProfileResponse mockResponse = TechnicalProfileResponse.builder()
                .id(profileId)
                .name("Pedro Dev")
                .email("pedro@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("85.00"))
                .currency("PEN")
                .isActive(false)
                .build();

        when(technicalProfileService.changeStatus(profileId, request, actorId)).thenReturn(mockResponse);

        ResponseEntity<TechnicalProfileResponse> response = controller.changeStatus(profileId, request, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().isActive());
        verify(technicalProfileService).changeStatus(profileId, request, actorId);
    }

    @Test
    void deleteShouldReturnNoContentStatus() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(actorId);

        ResponseEntity<Void> response = controller.delete(profileId, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(technicalProfileService).delete(profileId, actorId);
    }
}
