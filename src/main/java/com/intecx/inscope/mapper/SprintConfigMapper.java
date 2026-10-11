package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.entity.SprintConfig;
import org.springframework.stereotype.Component;

@Component
public class SprintConfigMapper implements BaseMapper<SprintConfig, SprintConfigResponse> {

    @Override
    public SprintConfigResponse toResponse(SprintConfig config) {
        if (config == null) {
            return null;
        }

        return SprintConfigResponse.builder()
                .projectId(config.getProjectId())
                .velocity(config.getVelocity())
                .duration(config.getDuration())
                .unit(config.getUnit())
                .build();
    }
}