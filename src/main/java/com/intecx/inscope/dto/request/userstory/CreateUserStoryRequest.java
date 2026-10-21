package com.intecx.inscope.dto.request.userstory;

import com.intecx.inscope.enumeration.StoryPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateUserStoryRequest(
        @NotBlank(message = "El código de la historia es obligatorio")
        @Size(max = 10, message = "El código no puede superar los 10 caracteres")
        @Schema(example = "US-101")
        String code,

        @NotBlank(message = "El título de la historia es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String title,

        @NotBlank(message = "La descripción de la historia es obligatoria")
        String description,

        String acceptanceCriteria,

        StoryPriority priority,

        @PositiveOrZero(message = "Los puntos de historia no pueden ser negativos")
        Short storyPoints
) {
}