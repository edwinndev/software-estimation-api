package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.SprintEstimationResponse;
import com.intecx.inscope.dto.response.TimeEstimationResponse;
import com.intecx.inscope.entity.SprintConfig;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class TimeEstimationMapper {

    public TimeEstimationResponse toResponse(
            SprintEstimationResponse sprints,
            SprintConfig config,
            long storiesWithPoints,
            long totalStories,
            long tasksWithHours,
            long totalTasks,
            long calendarTime,
            BigDecimal totalEffortHours
    ) {
        return TimeEstimationResponse.builder()
                .storiesWithPoints(storiesWithPoints)
                .totalStories(totalStories)
                .totalStoryPoints(sprints.getTotalStoryPoints())
                .velocity(sprints.getVelocity())
                .sprintDuration(config.getDuration())
                .sprintUnit(config.getUnit())
                .tasksWithHours(tasksWithHours)
                .totalTasks(totalTasks)
                .estimatedSprints(sprints.getEstimatedSprints())
                .calendarTime(calendarTime)
                .totalEffortHours(totalEffortHours)
                .build();
    }
}