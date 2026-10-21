package com.intecx.inscope.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TaskCostResponse(
        UUID taskId,
        CostAmountResponse total,
        List<TaskProfileCostResponse> contributions
) {
}
