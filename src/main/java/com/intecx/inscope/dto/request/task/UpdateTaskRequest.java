package com.intecx.inscope.dto.request.task;

import com.intecx.inscope.enumeration.TaskStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String title,

        String description,

        @Positive(message = "La estimación en horas debe ser mayor que cero")
        Integer estimateHours,

        TaskStatus status
) {
}