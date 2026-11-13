package com.intecx.estimation.dto.request;

import com.intecx.estimation.enumeration.UserStoryPriority;
import com.intecx.estimation.enumeration.UserStoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserStoryUpdateRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(min = 10) String description,
        @NotNull UserStoryPriority priority,
        @NotNull UserStoryStatus status,
        Integer position
) {

    public UserStoryUpdateRequest {
        title = title == null ? null : title.strip();
        description = description == null ? null : description.strip();
    }
}
