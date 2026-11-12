package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.ProjectStatus;
import com.intecx.inscope.enumeration.ProjectType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProjectResponse(
        UUID id,
        String name,
        String description,
        ProjectType type,
        LocalDate startDate,
        LocalDate plannedDate,
        UUID responsibleId,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProjectEstimationResponse> estimations
) {
}
