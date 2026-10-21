package com.intecx.inscope.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProjectCostResponse(
        UUID projectId,
        CostAmountResponse total,
        List<TaskCostResponse> tasks,
        List<ProfileCostResponse> profiles
) {
}
