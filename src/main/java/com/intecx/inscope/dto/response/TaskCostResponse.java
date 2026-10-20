package com.intecx.inscope.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskCostResponse {
    UUID taskId;
    CostAmountResponse total;
    List<TaskProfileCostResponse> contributions;
}
