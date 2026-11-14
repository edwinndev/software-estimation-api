package com.intecx.inscope.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record AiAllocationResponse(
        String algorithm,
        List<AssignmentPayload> assignments,
        @JsonProperty("total_cost") BigDecimal totalCost,
        @JsonProperty("nodes_explored") int nodesExplored,
        @JsonProperty("execution_time_ms") double executionTimeMs,
        @JsonProperty("optimal_guaranteed") boolean optimalGuaranteed
) {
    public record AssignmentPayload(
            @JsonProperty("task_id") String taskId,
            @JsonProperty("task_title") String taskTitle,
            @JsonProperty("profile_id") String profileId,
            @JsonProperty("profile_name") String profileName,
            @JsonProperty("estimate_hours") int estimateHours,
            @JsonProperty("hourly_rate") BigDecimal hourlyRate,
            BigDecimal cost
    ) {
    }
}

