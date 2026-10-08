package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.service.TechnicalProfileService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechnicalProfileServiceImpl implements TechnicalProfileService {

    private final TechnicalProfileRepository technicalProfileRepository;
    private final TechnicalProfileMapper technicalProfileMapper;

    @Override
    @Transactional
    public TechnicalProfileResponse create(CreateTechnicalProfileRequest request, UUID actorId) {
        String email = request.email().trim().toLowerCase();
        if (technicalProfileRepository.existsActiveByEmail(email)) {
            throw new ConflictException("Ya existe un perfil técnico registrado con el correo electrónico proporcionado");
        }

        TechnicalProfile technicalProfile = technicalProfileMapper.toEntity(request, actorId);
        TechnicalProfile savedProfile = technicalProfileRepository.save(technicalProfile);

        return technicalProfileMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional
    public TechnicalProfileResponse updateCer(UUID id, UpdateCerRequest request, UUID actorId) {
        TechnicalProfile profile = technicalProfileRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        profile.setHourlyRate(request.hourlyRate());
        if (request.currency() != null && !request.currency().isBlank()) {
            profile.setCurrency(request.currency().trim().toUpperCase());
        }
        profile.setUpdatedBy(actorId);

        TechnicalProfile updatedProfile = technicalProfileRepository.save(profile);
        return technicalProfileMapper.toResponse(updatedProfile);
    }
}
