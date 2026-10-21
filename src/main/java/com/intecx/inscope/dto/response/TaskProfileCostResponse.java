package com.intecx.inscope.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TaskProfileCostResponse(
        UUID technicalProfileId,
        BigDecimal hours,
        BigDecimal hourlyRate,
        CostAmountResponse cost
) {
}
