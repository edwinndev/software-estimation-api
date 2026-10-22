package com.intecx.inscope.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.ToggleProfileStatusRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.request.profile.UpdateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
                .currency(CurrencyCode.PEN)
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
        assertEquals(CurrencyCode.PEN, response.currency());
        assertTrue(response.isActive());

        ArgumentCaptor<TechnicalProfile> captor = ArgumentCaptor.forClass(TechnicalProfile.class);
        verify(technicalProfileRepository).save(captor.capture());
        TechnicalProfile saved = captor.getValue();
        assertEquals(actorId, saved.getCreatedBy());
        assertEquals(role, saved.getRole());
        assertEquals(new BigDecimal("80.00"), saved.getHourlyRate());
    }

    @Test
    void updateCerShouldUpdateHourlyRateAndCurrency() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalProfile existing = TechnicalProfile.builder()
                .id(profileId)
                .name("Sara Dev")
                .email("sara@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("65.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        UpdateCerRequest request = UpdateCerRequest.builder()
                .hourlyRate(new BigDecimal("95.00"))
                .currency(CurrencyCode.PEN)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(existing));
        when(technicalProfileRepository.save(any(TechnicalProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        TechnicalProfileResponse response = technicalProfileService.updateCer(profileId, request, actorId);

        assertNotNull(response);
        assertEquals(new BigDecimal("95.00"), response.hourlyRate());
        assertEquals(CurrencyCode.PEN, response.currency());
        assertEquals(actorId, existing.getUpdatedBy());
        verify(technicalProfileRepository).save(existing);
    }

    @Test
    void updateCerShouldThrowResourceNotFoundExceptionWhenProfileDoesNotExist() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UpdateCerRequest request = UpdateCerRequest.builder()
                .hourlyRate(new BigDecimal("100.00"))
                .currency(CurrencyCode.PEN)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> technicalProfileService.updateCer(profileId, request, actorId));
    }

    @Test
    void updateShouldUpdateProfileMetadataAndCer() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalProfile existing = TechnicalProfile.builder()
                .id(profileId)
                .name("Old Name")
                .email("old@example.com")
                .role(TechnicalRole.QA)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .hourlyRate(new BigDecimal("40.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        UpdateTechnicalProfileRequest request = UpdateTechnicalProfileRequest.builder()
                .name("New Name")
                .email("new@example.com")
                .role(TechnicalRole.QA)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("55.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(existing));
        when(technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNullAndIdNot("new@example.com", profileId))
                .thenReturn(false);
        when(technicalProfileRepository.save(any(TechnicalProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        TechnicalProfileResponse response = technicalProfileService.update(profileId, request, actorId);

        assertNotNull(response);
        assertEquals("New Name", response.name());
        assertEquals("new@example.com", response.email());
        assertEquals(ExperienceLevel.MID, response.experienceLevel());
        assertEquals(new BigDecimal("55.00"), response.hourlyRate());
        assertEquals(actorId, existing.getUpdatedBy());
    }

    @Test
    void getByIdShouldReturnProfileWhenFound() {
        UUID profileId = UUID.randomUUID();
        TechnicalProfile entity = TechnicalProfile.builder()
                .id(profileId)
                .name("Lucia Gomez")
                .email("lucia@example.com")
                .role(TechnicalRole.TECH_LEAD)
                .experienceLevel(ExperienceLevel.LEAD)
                .hourlyRate(new BigDecimal("150.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(entity));

        TechnicalProfileResponse response = technicalProfileService.getById(profileId);

        assertNotNull(response);
        assertEquals(profileId, response.id());
        assertEquals("Lucia Gomez", response.name());
        assertEquals(new BigDecimal("150.00"), response.hourlyRate());
    }

    @Test
    void getByIdShouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID profileId = UUID.randomUUID();
        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> technicalProfileService.getById(profileId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchShouldReturnPaginatedProfilesWithCer() {
        UUID profileId = UUID.randomUUID();
        TechnicalProfile entity = TechnicalProfile.builder()
                .id(profileId)
                .name("Martin Dev")
                .email("martin@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.SENIOR)
                .hourlyRate(new BigDecimal("95.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        when(technicalProfileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        QueryRequest queryRequest = new QueryRequest(null, null);
        PaginatedResponse<TechnicalProfileResponse> response = technicalProfileService.search(queryRequest);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());
        TechnicalProfileResponse item = response.getItems().getFirst();
        assertEquals(profileId, item.id());
        assertEquals(new BigDecimal("95.00"), item.hourlyRate());
    }

    @Test
    void createShouldThrowConflictExceptionWhenEmailAlreadyExists() {
        UUID actorId = UUID.randomUUID();
        CreateTechnicalProfileRequest request = CreateTechnicalProfileRequest.builder()
                .name("Duplicate User")
                .email("duplicate@example.com")
                .role(TechnicalRole.BACKEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("80.00"))
                .currency(CurrencyCode.PEN)
                .build();

        when(technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull("duplicate@example.com"))
                .thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> technicalProfileService.create(request, actorId)
        );
        assertEquals("Ya existe un perfil técnico registrado con el correo electrónico proporcionado", ex.getMessage());
    }

    @Test
    void updateShouldThrowConflictExceptionWhenEmailAlreadyExistsOnAnotherProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalProfile existing = TechnicalProfile.builder()
                .id(profileId)
                .name("Original Name")
                .email("original@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("70.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        UpdateTechnicalProfileRequest request = UpdateTechnicalProfileRequest.builder()
                .name("Updated Name")
                .email("existing.other@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("70.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(existing));
        when(technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNullAndIdNot("existing.other@example.com", profileId))
                .thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> technicalProfileService.update(profileId, request, actorId)
        );
        assertEquals("Ya existe un perfil técnico registrado con el correo electrónico proporcionado", ex.getMessage());
    }

    @Test
    void changeStatusShouldToggleActiveAndSetUpdatedBy() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalProfile existing = TechnicalProfile.builder()
                .id(profileId)
                .name("Status Test")
                .email("status@example.com")
                .role(TechnicalRole.QA)
                .experienceLevel(ExperienceLevel.MID)
                .hourlyRate(new BigDecimal("60.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        ToggleProfileStatusRequest request = ToggleProfileStatusRequest.builder()
                .isActive(false)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(existing));
        when(technicalProfileRepository.save(any(TechnicalProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        TechnicalProfileResponse response = technicalProfileService.changeStatus(profileId, request, actorId);

        assertNotNull(response);
        assertFalse(response.isActive());
        assertFalse(existing.isActive());
        assertEquals(actorId, existing.getUpdatedBy());
        verify(technicalProfileRepository).save(existing);
    }

    @Test
    void changeStatusShouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        ToggleProfileStatusRequest request = ToggleProfileStatusRequest.builder()
                .isActive(false)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> technicalProfileService.changeStatus(profileId, request, actorId));
    }

    @Test
    void deleteShouldPerformSoftDeleteAndDeactivateProfile() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        TechnicalProfile existing = TechnicalProfile.builder()
                .id(profileId)
                .name("To Delete")
                .email("delete@example.com")
                .role(TechnicalRole.FRONTEND)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .hourlyRate(new BigDecimal("45.00"))
                .currency(CurrencyCode.PEN)
                .isActive(true)
                .build();

        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.of(existing));

        technicalProfileService.delete(profileId, actorId);

        assertNotNull(existing.getDeletedAt());
        assertEquals(actorId, existing.getDeletedBy());
        assertFalse(existing.isActive());
        verify(technicalProfileRepository).save(existing);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        when(technicalProfileRepository.findByIdAndDeletedAtIsNull(profileId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> technicalProfileService.delete(profileId, actorId));
    }
}
