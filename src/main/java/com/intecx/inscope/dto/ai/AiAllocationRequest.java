package com.intecx.inscope.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.enumeration.TechnicalRole;
import java.math.BigDecimal;
import java.util.List;

public record AiAllocationRequest(
        List<TaskPayload> tasks,
        List<ProfilePayload> profiles,
        @JsonProperty("max_hours_per_profile") Double maxHoursPerProfile,
        String algorithm
) {
    public record TaskPayload(
            String id,
            String title,
            @JsonProperty("estimate_hours") int estimateHours
    ) {
    }

    public record ProfilePayload(
            String id,
            String name,
            TechnicalRole role,
            @JsonProperty("experience_level") ExperienceLevel experienceLevel,
            @JsonProperty("hourly_rate") BigDecimal hourlyRate
    ) {
    }
}

