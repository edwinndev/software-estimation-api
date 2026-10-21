package com.intecx.inscope.dto.request.userstory;

import com.intecx.inscope.enumeration.StoryPriority;
import com.intecx.inscope.enumeration.StoryStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateUserStoryRequest(
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        @Pattern(regexp = ".*\\S.*", message = "El título no puede estar vacío")
        String title,

        @Pattern(regexp = ".*\\S.*", message = "La descripción no puede estar vacía")
        String description,

        String acceptanceCriteria,

        StoryPriority priority,

        StoryStatus status,

        @PositiveOrZero(message = "Los puntos de historia no pueden ser negativos")
        Short storyPoints
) {
}