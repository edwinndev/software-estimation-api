package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.project.RiskLevelConfigResponse;
import com.intecx.inscope.entity.RiskLevelConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RiskLevelConfigMapper {

    public RiskLevelConfigResponse toResponse(RiskLevelConfig entity) {
        if (entity == null) {
            return null;
        }
        return RiskLevelConfigResponse.builder()
                .riskLevel(entity.getRiskLevel())
                .contingencyPercentage(entity.getContingencyPercentage())
                .build();
    }
}
