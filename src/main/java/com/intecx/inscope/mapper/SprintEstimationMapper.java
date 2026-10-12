package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.SprintEstimationResponse;
import org.springframework.stereotype.Component;

@Component
public class SprintEstimationMapper {

    public SprintEstimationResponse toResponse(long totalStoryPoints, int velocity, long estimatedSprints) {
        return SprintEstimationResponse.builder()
                .totalStoryPoints(totalStoryPoints)
                .velocity(velocity)
                .estimatedSprints(estimatedSprints)
                .build();
    }
}