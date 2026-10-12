package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.ProjectStatus;
import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
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
        String type,
        LocalDate startDate,
        LocalDate plannedDate,
        UUID responsibleId,
        ProjectStatus status,
        RiskLevel riskLevel,
        BigDecimal contingencyMargin,
        LocalDateTime createdAt,
        List<ProjectEstimationResponse> estimations
) {
}
