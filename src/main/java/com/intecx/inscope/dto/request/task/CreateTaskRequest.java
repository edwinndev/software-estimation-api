package com.intecx.inscope.dto.request.task;

import com.intecx.inscope.enumeration.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank(message = "El título de la tarea es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String title,

        @NotBlank(message = "La descripción de la tarea es obligatoria")
        String description,

        @NotNull(message = "La estimación en horas es obligatoria")
        @Positive(message = "La estimación en horas debe ser mayor que cero")
        Integer estimateHours,

        TaskStatus status,

        @Schema(description = "Identificadores únicos de perfiles técnicos activos")
        List<UUID> responsibleProfileIds
) {
}