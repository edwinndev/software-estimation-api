package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.TechnicalProfile;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TechnicalProfileMapper implements BaseMapper<TechnicalProfile, TechnicalProfileResponse> {

    @Override
    public TechnicalProfileResponse toResponse(TechnicalProfile entity) {
        if (entity == null) {
            return null;
        }

        return TechnicalProfileResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .experienceLevel(entity.getExperienceLevel())
                .hourlyRate(entity.getHourlyRate())
                .currency(entity.getCurrency())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public TechnicalProfile toEntity(CreateTechnicalProfileRequest request, UUID actorId) {
        if (request == null) {
            return null;
        }

        boolean activeStatus = request.getIsActive() == null || request.getIsActive();
        String currencyCode = request.getCurrency() != null && !request.getCurrency().isBlank()
                ? request.getCurrency().trim().toUpperCase()
                : "PEN";

        return TechnicalProfile.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .role(request.getRole())
                .experienceLevel(request.getExperienceLevel())
                .hourlyRate(request.getHourlyRate())
                .currency(currencyCode)
                .isActive(activeStatus)
                .createdBy(actorId)
                .build();
    }
}
