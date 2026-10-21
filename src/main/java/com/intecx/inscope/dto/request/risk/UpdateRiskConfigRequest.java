package com.intecx.inscope.dto.request.risk;

import com.intecx.inscope.enumeration.RiskLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRiskConfigRequest {

    @NotNull(message = "El nivel de riesgo es obligatorio")
    private RiskLevel level;

    @DecimalMin(value = "0.00", message = "El margen no puede ser negativo")
    @DecimalMax(value = "100.00", message = "El margen no puede superar 100")
    private BigDecimal contingencyMargin;
}
