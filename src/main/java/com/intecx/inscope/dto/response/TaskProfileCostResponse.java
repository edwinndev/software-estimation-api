package com.intecx.inscope.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskProfileCostResponse {
    UUID technicalProfileId;
    BigDecimal hours;
    BigDecimal hourlyRate;
    CostAmountResponse cost;
}
