package com.intecx.inscope.dto.response;

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
        String timeUnit,
        BigDecimal totalCost,
        LocalDateTime createdAt
) {
}
