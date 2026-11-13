package com.intecx.estimation.dto.request;

import com.intecx.estimation.enumeration.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TechnicalTaskUpdateRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(min = 10) String description,
        @Positive int estimateHours,
        @NotNull TaskStatus status,
        Integer position
) {

    public TechnicalTaskUpdateRequest {
        title = title == null ? null : title.strip();
        description = description == null ? null : description.strip();
    }
}
