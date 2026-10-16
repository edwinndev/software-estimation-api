package com.intecx.inscope.dto.request.userstory;

import com.intecx.inscope.enumeration.StoryPriority;
import com.intecx.inscope.enumeration.StoryStatus;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateUserStoryRequest(
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String title,

        String description,

        String acceptanceCriteria,

        StoryPriority priority,

        StoryStatus status,

        @PositiveOrZero(message = "Los puntos de historia no pueden ser negativos")
        Short storyPoints
) {
}