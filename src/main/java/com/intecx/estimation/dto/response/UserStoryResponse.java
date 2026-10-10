package com.intecx.estimation.dto.response;

import com.intecx.estimation.enumeration.StoryPriority;
import com.intecx.estimation.enumeration.StoryStatus;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStoryResponse {

    UUID id;
    UUID projectId;
    String code;
    String title;
    String description;
    StoryPriority priority;
    StoryStatus status;
    Short storyPoints;
    Integer position;
}