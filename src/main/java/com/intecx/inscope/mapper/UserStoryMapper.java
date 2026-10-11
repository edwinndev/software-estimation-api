package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.entity.UserStory;
import org.springframework.stereotype.Component;

@Component
public class UserStoryMapper implements BaseMapper<UserStory, UserStoryResponse> {

    @Override
    public UserStoryResponse toResponse(UserStory story) {
        if (story == null) {
            return null;
        }

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