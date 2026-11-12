package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.CurrencyCode;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CostAmountResponse(
        BigDecimal amount,
        CurrencyCode currency
) {
}
