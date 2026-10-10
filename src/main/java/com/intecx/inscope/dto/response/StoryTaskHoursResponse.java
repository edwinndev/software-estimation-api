package com.intecx.inscope.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Horas por tarea y perfil de una historia de usuario")
public class StoryTaskHoursResponse {

    @Schema(description = "Identificador de la historia")
    UUID storyId;

    @Schema(description = "Código de la historia", example = "HU-01")
    String code;

    @Schema(description = "Título de la historia")
    String title;

    @Schema(description = "Suma de las horas de la historia", example = "4.00")
    BigDecimal totalHours;

    @Schema(description = "Una fila por cada combinación de tarea y perfil técnico")
    List<TaskProfileHoursResponse> rows;
}