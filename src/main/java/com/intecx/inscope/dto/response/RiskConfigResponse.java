package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record RiskConfigResponse(
        RiskLevel level,
        BigDecimal contingencyMargin
) {
}
