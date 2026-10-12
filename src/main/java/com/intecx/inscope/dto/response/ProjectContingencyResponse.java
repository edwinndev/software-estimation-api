package com.intecx.inscope.dto.response.project;

import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProjectContingencyResponse(
        RiskLevel riskLevel,
        BigDecimal contingencyMargin,
        BigDecimal estimatedTime,
        BigDecimal estimatedTimeWithContingency,
        BigDecimal estimatedCost,
        BigDecimal estimatedCostWithContingency
) {
}
