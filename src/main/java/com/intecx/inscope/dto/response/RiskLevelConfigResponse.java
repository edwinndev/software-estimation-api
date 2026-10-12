package com.intecx.inscope.dto.response.project;

import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RiskLevelConfigResponse {

    private final RiskLevel riskLevel;
    private final BigDecimal contingencyPercentage;
}
