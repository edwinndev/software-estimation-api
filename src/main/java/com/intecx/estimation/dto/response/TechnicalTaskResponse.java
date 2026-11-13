package com.intecx.estimation.dto.response;

import com.intecx.estimation.enumeration.TaskStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TechnicalTaskResponse(
        UUID id,
        UUID storyId,
        String title,
        String description,
        int estimateHours,
        TaskStatus status,
        int position,
        List<UUID> profileIds,
        Instant createdAt,
        Instant updatedAt
) {
}
