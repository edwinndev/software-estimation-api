package com.intecx.inscope.dto.request.project;

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
public class ProjectContingencyRequest {

    @NotNull(message = "El tiempo estimado es obligatorio")
    @DecimalMin(value = "0.00", message = "El tiempo estimado no puede ser negativo")
    private BigDecimal estimatedTime;

    @NotNull(message = "El costo estimado es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo estimado no puede ser negativo")
    private BigDecimal estimatedCost;
}
