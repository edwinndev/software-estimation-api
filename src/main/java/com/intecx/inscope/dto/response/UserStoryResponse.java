package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.StoryPriority;
import com.intecx.inscope.enumeration.StoryStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserStoryResponse(
        UUID id,
        UUID projectId,
        String code,
        String title,
        String description,
        String acceptanceCriteria,
        StoryPriority priority,
        StoryStatus status,
        Short storyPoints,
        Integer position,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}