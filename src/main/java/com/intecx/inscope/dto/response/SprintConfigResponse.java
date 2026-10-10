package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.SprintUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Configuración de sprints de un proyecto")
public class SprintConfigResponse {

    @Schema(description = "Identificador del proyecto")
    UUID projectId;

    @Schema(description = "Story points que el equipo completa por sprint", example = "20")
    Integer velocity;

    @Schema(description = "Duración de cada sprint, expresada en la unidad indicada", example = "2")
    Integer duration;

    @Schema(description = "Unidad de la duración del sprint")
    SprintUnit unit;
}