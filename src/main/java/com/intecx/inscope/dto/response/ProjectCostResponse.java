package com.intecx.inscope.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectCostResponse {
    UUID projectId;
    CostAmountResponse total;
    List<TaskCostResponse> tasks;
    List<ProfileCostResponse> profiles;
}
