package com.intecx.inscope.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intecx.inscope.enumeration.ProjectType;
import java.math.BigDecimal;

public record AiRiskPredictionRequest(
        @JsonProperty("project_type") ProjectType projectType,
        @JsonProperty("base_effort_points") int baseEffortPoints,
        @JsonProperty("total_effort_hours") BigDecimal totalEffortHours,
        @JsonProperty("stories_total") int storiesTotal,
        @JsonProperty("tasks_total") int tasksTotal,
        @JsonProperty("total_sprints") int totalSprints,
        @JsonProperty("planned_days") long plannedDays,
        @JsonProperty("junior_ratio") double juniorRatio,
        @JsonProperty("senior_ratio") double seniorRatio
) {
}

