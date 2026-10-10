package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.entity.SprintConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SprintConfigMapper {

    public SprintConfigResponse toResponse(SprintConfig config) {
        return SprintConfigResponse.builder()
                .projectId(config.getProjectId())
                .velocity(config.getVelocity())
                .duration(config.getDuration())
                .unit(config.getUnit())
                .build();
    }
}