package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TechnicalProfileResponse(
        UUID id,
        String name,
        String email,
        TechnicalRole role,
        ExperienceLevel experienceLevel,
        BigDecimal hourlyRate,
        String currency,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
