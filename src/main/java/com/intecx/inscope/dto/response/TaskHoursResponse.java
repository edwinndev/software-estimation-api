package com.intecx.inscope.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Horas estimadas por tarea de un proyecto y esfuerzo total")
public class TaskHoursResponse {

    @Schema(description = "Esfuerzo total del proyecto, en horas", example = "4.00")
    BigDecimal totalHours;

    @Schema(description = "Historias con sus horas por tarea y perfil")
    List<StoryTaskHoursResponse> stories;
}