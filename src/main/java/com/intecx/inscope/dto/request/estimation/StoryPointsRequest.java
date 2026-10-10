package com.intecx.inscope.dto.request.estimation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Story points a asignar a una historia de usuario")
public record StoryPointsRequest(
        @Schema(description = "Cantidad de story points (entero positivo)", example = "5")
        @NotNull(message = "Los story points son obligatorios")
        @Positive(message = "Los story points deben ser un número positivo")
        @Max(value = Short.MAX_VALUE, message = "Los story points superan el máximo permitido")
        Integer storyPoints
) {
}