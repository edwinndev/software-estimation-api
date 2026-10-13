package com.intecx.inscope.dto.response.project;

import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProjectContingencyResponse(
        RiskLevel riskLevel,
        BigDecimal contingencyMarginPercentage,
        BigDecimal baseTime,
        BigDecimal contingencyTime,
        BigDecimal totalTime,
        BigDecimal baseCost,
        BigDecimal contingencyCost,
        BigDecimal totalCost
) {
}
