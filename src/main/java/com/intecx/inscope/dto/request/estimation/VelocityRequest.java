package com.intecx.inscope.dto.request.estimation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Velocidad del equipo obtenida en sprints anteriores")
public record VelocityRequest(
        @Schema(description = "Story points que el equipo completa por sprint", example = "20")
        @NotNull(message = "La velocidad es obligatoria")
        @Positive(message = "La velocidad debe ser un número positivo")
        Integer velocity
) {
}