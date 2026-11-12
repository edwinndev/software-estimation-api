package com.intecx.estimation.dto.response;

import com.intecx.estimation.enumeration.UserStoryPriority;
import com.intecx.estimation.enumeration.UserStoryStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStoryResponse(
        UUID id,
        UUID projectId,
        String code,
        String title,
        String description,
        UserStoryPriority priority,
        UserStoryStatus status,
        Short storyPoints,
        int position,
        Instant createdAt,
        Instant updatedAt
) {
}
