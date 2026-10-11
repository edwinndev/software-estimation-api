package com.intecx.inscope.dto.response;

import java.math.BigDecimal;

public record CostAmountResponse(BigDecimal amount, String currency) {
}
