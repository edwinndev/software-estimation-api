package com.intecx.inscope.dto.request.project;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ContingencyConfigRequest(
        @NotNull(message = "El porcentaje de contingencia es obligatorio")
        @DecimalMin(value = "0.00", message = "El porcentaje no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
        BigDecimal contingencyPercentage
) {
}
