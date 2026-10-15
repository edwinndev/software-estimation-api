package com.intecx.inscope.dto.request.project;

import com.intecx.inscope.enumeration.RiskLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RiskLevelUpdateRequest(
        @NotNull(message = "El nivel de riesgo es obligatorio")
        RiskLevel level,

        @DecimalMin(value = "0.00", message = "El margen no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El margen no puede superar 100")
        BigDecimal contingencyMargin
) {
}
