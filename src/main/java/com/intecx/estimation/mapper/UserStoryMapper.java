package com.intecx.estimation.mapper;

import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.entity.UserStory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserStoryMapper {

    public UserStoryResponse toResponse(UserStory story) {
        return UserStoryResponse.builder()
                .id(story.getId())
                .projectId(story.getProjectId())
                .code(story.getCode())
                .title(story.getTitle())
                .description(story.getDescription())
                .priority(story.getPriority())
                .status(story.getStatus())
                .storyPoints(story.getStoryPoints())
                .position(story.getPosition())
                .build();
    }
}