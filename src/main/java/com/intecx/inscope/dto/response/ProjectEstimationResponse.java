package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.SprintUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProjectEstimationResponse(
        UUID id,
        Integer baseEffortPoints,
        BigDecimal totalEffortHours,
        BigDecimal totalTime,
        SprintUnit timeUnit,
        BigDecimal totalCost,
        LocalDateTime createdAt
) {
}
