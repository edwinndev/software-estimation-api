package com.intecx.inscope.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CostAmountResponse(
        BigDecimal amount,
        String currency
) {
}
