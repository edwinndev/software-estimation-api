package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.CostAmountResponse;
import java.util.UUID;

public record ProfileCostContribution(UUID technicalProfileId, CostAmountResponse cost) {
}
