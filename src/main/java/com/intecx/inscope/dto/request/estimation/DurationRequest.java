package com.intecx.inscope.dto.request.estimation;

import com.intecx.inscope.enumeration.SprintUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Duración del sprint utilizada en las estimaciones")
public record DurationRequest(
        @Schema(description = "Duración de cada sprint, en la unidad indicada", example = "2")
        @NotNull(message = "La duración es obligatoria")
        @Positive(message = "La duración debe ser un número positivo")
        Integer duration,

        @Schema(description = "Unidad de la duración del sprint", example = "WEEKS")
        @NotNull(message = "La unidad es obligatoria")
        SprintUnit unit
) {
}