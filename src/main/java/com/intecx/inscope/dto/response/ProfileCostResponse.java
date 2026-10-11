package com.intecx.inscope.dto.response;

import java.util.UUID;

public record ProfileCostResponse(UUID technicalProfileId, CostAmountResponse subtotal) {
}
