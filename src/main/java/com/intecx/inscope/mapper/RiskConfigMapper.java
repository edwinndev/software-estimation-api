package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.RiskConfigResponse;
import com.intecx.inscope.entity.RiskConfig;
import org.springframework.stereotype.Component;

@Component
public class RiskConfigMapper {

    public RiskConfigResponse toResponse(RiskConfig entity) {
        if (entity == null) {
            return null;
        }
        return RiskConfigResponse.builder()
                .level(entity.getLevel())
                .contingencyMargin(entity.getContingencyMargin())
                .build();
    }
}
