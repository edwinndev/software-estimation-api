package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryFields;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.common.QuerySupport;
import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.ToggleProfileStatusRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.request.profile.UpdateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.service.TechnicalProfileService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechnicalProfileServiceImpl implements TechnicalProfileService {

    private static final QueryFields PROFILE_FIELDS = QueryFields.sortingBy("createdAt")
            .filter("search", "name", "email")
            .filter("name", "name")
            .filter("email", "email")
            .filter("role", "role")
            .filter("experienceLevel", "experienceLevel")
            .filter("hourlyRate", "hourlyRate")
            .filter("currency", "currency")
            .filter("isActive", "isActive")
            .filter("createdAt", "createdAt")
            .sortable("createdAt", "name", "email", "role", "experienceLevel", "hourlyRate", "isActive");

    private final TechnicalProfileRepository technicalProfileRepository;
    private final TechnicalProfileMapper technicalProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TechnicalProfileResponse> search(QueryRequest query) {
        Specification<TechnicalProfile> notDeletedScope = (root, q, cb) -> cb.isNull(root.get("deletedAt"));
        QueryRequest searchQuery = query == null ? new QueryRequest(null, null) : query;
        return QuerySupport.search(
                technicalProfileRepository,
                notDeletedScope,
                PROFILE_FIELDS,
                searchQuery,
                technicalProfileMapper::toResponse,
                "profilesResponse"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TechnicalProfileResponse getById(UUID id) {
        TechnicalProfile profile = technicalProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));
        return technicalProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public TechnicalProfileResponse create(CreateTechnicalProfileRequest request, UUID actorId) {
        String email = request.getEmail().trim().toLowerCase();
        if (technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new ConflictException("Ya existe un perfil técnico registrado con el correo electrónico proporcionado");
        }

        TechnicalProfile technicalProfile = technicalProfileMapper.toEntity(request, actorId);
        TechnicalProfile savedProfile = technicalProfileRepository.save(technicalProfile);

        return technicalProfileMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional
    public TechnicalProfileResponse update(UUID id, UpdateTechnicalProfileRequest request, UUID actorId) {
        TechnicalProfile profile = technicalProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!profile.getEmail().equalsIgnoreCase(newEmail)
                && technicalProfileRepository.existsByEmailIgnoreCaseAndDeletedAtIsNullAndIdNot(newEmail, id)) {
            throw new ConflictException("Ya existe un perfil técnico registrado con el correo electrónico proporcionado");
        }

        profile.setName(request.getName().trim());
        profile.setEmail(newEmail);
        profile.setRole(request.getRole());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setHourlyRate(request.getHourlyRate());
        if (request.getCurrency() != null) {
            profile.setCurrency(request.getCurrency());
        }
        if (request.getIsActive() != null) {
            profile.setActive(request.getIsActive());
        }
        profile.setUpdatedBy(actorId);

        TechnicalProfile updatedProfile = technicalProfileRepository.save(profile);
        return technicalProfileMapper.toResponse(updatedProfile);
    }

    @Override
    @Transactional
    public TechnicalProfileResponse updateCer(UUID id, UpdateCerRequest request, UUID actorId) {
        TechnicalProfile profile = technicalProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        profile.setHourlyRate(request.getHourlyRate());
        if (request.getCurrency() != null) {
            profile.setCurrency(request.getCurrency());
        }
        profile.setUpdatedBy(actorId);

        TechnicalProfile updatedProfile = technicalProfileRepository.save(profile);
        return technicalProfileMapper.toResponse(updatedProfile);
    }

    @Override
    @Transactional
    public TechnicalProfileResponse changeStatus(UUID id, ToggleProfileStatusRequest request, UUID actorId) {
        TechnicalProfile profile = technicalProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        profile.setActive(request.getIsActive());
        profile.setUpdatedBy(actorId);

        TechnicalProfile updatedProfile = technicalProfileRepository.save(profile);
        return technicalProfileMapper.toResponse(updatedProfile);
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID actorId) {
        TechnicalProfile profile = technicalProfileRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        profile.setDeletedAt(LocalDateTime.now());
        profile.setDeletedBy(actorId);
        profile.setActive(false);

        technicalProfileRepository.save(profile);
    }
}
