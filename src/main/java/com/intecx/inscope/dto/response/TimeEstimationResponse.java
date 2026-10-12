package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.SprintUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Cálculo del tiempo estimado del proyecto con sus datos de entrada")
public class TimeEstimationResponse {

    @Schema(description = "Historias activas con story points asignados", example = "1")
    Long storiesWithPoints;

    @Schema(description = "Historias activas del proyecto", example = "1")
    Long totalStories;

    @Schema(description = "Suma de los story points de las historias activas", example = "13")
    Long totalStoryPoints;

    @Schema(description = "Velocidad del equipo en story points por sprint", example = "5")
    Integer velocity;

    @Schema(description = "Duración de cada sprint, en la unidad indicada", example = "2")
    Integer sprintDuration;

    @Schema(description = "Unidad de la duración del sprint")
    SprintUnit sprintUnit;

    @Schema(description = "Tareas activas con horas asignadas a algún perfil", example = "1")
    Long tasksWithHours;

    @Schema(description = "Tareas activas del proyecto", example = "1")
    Long totalTasks;

    @Schema(description = "Sprints necesarios: story points entre velocidad, redondeado hacia arriba", example = "3")
    Long estimatedSprints;

    @Schema(description = "Tiempo calendario: sprints por duración, en la unidad del sprint", example = "6")
    Long calendarTime;

    @Schema(description = "Esfuerzo total: suma de las horas de las tareas", example = "4.00")
    BigDecimal totalEffortHours;
}