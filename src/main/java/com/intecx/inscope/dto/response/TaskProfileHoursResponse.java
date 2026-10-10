package com.intecx.inscope.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Horas de un perfil técnico en una tarea")
public class TaskProfileHoursResponse {

    @Schema(description = "Identificador de la tarea")
    UUID taskId;

    @Schema(description = "Título de la tarea")
    String taskTitle;

    @Schema(description = "Identificador del perfil técnico")
    UUID technicalProfileId;

    @Schema(description = "Nombre del perfil técnico")
    String profileName;

    @Schema(description = "Horas del perfil en la tarea", example = "4.00")
    BigDecimal hours;

    @Schema(description = "De dónde salen las horas: reparto automático o el motivo del ajuste manual")
    String origin;
}