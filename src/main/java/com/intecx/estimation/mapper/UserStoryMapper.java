package com.intecx.estimation.mapper;

import com.intecx.estimation.dto.response.UserStoryResponse;
import com.intecx.estimation.entity.UserStory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserStoryMapper {

    public UserStoryResponse toResponse(UserStory story) {
        return new UserStoryResponse(
                story.getId(),
                story.getProjectId(),
                story.getCode(),
                story.getTitle(),
                story.getDescription(),
                story.getPriority(),
                story.getStatus(),
                story.getStoryPoints(),
                story.getPosition(),
                story.getCreatedAt(),
                story.getUpdatedAt()
        );
    }
}
