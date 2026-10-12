package com.intecx.inscope.dto.request.project;

import com.intecx.inscope.enumeration.RiskLevel;
import jakarta.validation.constraints.NotNull;

public record RiskLevelUpdateRequest(
        @NotNull(message = "El nivel de riesgo es obligatorio")
        RiskLevel riskLevel
) {
}
