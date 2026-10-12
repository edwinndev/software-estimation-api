package com.intecx.inscope.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Puntos de historia totales y sprints estimados de un proyecto")
public class SprintEstimationResponse {

    @Schema(description = "Suma de los story points de las historias activas", example = "13")
    Long totalStoryPoints;

    @Schema(description = "Velocidad del equipo usada en el cálculo", example = "5")
    Integer velocity;

    @Schema(description = "Sprints necesarios para completar los puntos (redondeado hacia arriba)", example = "3")
    Long estimatedSprints;
}