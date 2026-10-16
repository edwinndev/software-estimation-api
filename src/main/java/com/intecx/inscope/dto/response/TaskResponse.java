package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TaskResponse(
        UUID id,
        UUID projectId,
        UUID userStoryId,
        String title,
        String description,
        Integer estimateHours,
        TaskStatus status,
        Integer position,
        List<UUID> responsibleProfileIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}