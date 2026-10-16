package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.request.userstory.CreateUserStoryRequest;
import com.intecx.inscope.dto.request.userstory.UpdateUserStoryRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.StoryPriority;
import com.intecx.inscope.enumeration.StoryStatus;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserStoryMapper {

    public UserStoryResponse toResponse(UserStory story) {
        return UserStoryResponse.builder()
                .id(story.getId())
                .projectId(story.getProject().getId())
                .code(story.getCode())
                .title(story.getTitle())
                .description(story.getDescription())
                .acceptanceCriteria(story.getAcceptanceCriteria())
                .priority(story.getPriority())
                .status(story.getStatus())
                .storyPoints(story.getStoryPoints())
                .position(story.getPosition())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }

    public UserStory toEntity(
            CreateUserStoryRequest request,
            Project project,
            int position,
            UUID actorId
    ) {
        return UserStory.builder()
                .project(project)
                .code(request.code().trim().toUpperCase())
                .title(request.title().trim())
                .description(request.description().trim())
                .acceptanceCriteria(normalizeOptional(request.acceptanceCriteria()))
                .priority(request.priority() == null ? StoryPriority.MEDIUM : request.priority())
                .status(StoryStatus.DRAFT)
                .storyPoints(request.storyPoints())
                .position(position)
                .createdBy(actorId)
                .build();
    }

    public void updateEntity(UserStory story, UpdateUserStoryRequest request, UUID actorId) {
        if (request.title() != null) {
            story.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            story.setDescription(request.description().trim());
        }
        if (request.acceptanceCriteria() != null) {
            story.setAcceptanceCriteria(request.acceptanceCriteria().trim());
        }
        if (request.priority() != null) {
            story.setPriority(request.priority());
        }
        if (request.status() != null) {
            story.setStatus(request.status());
        }
        if (request.storyPoints() != null) {
            story.setStoryPoints(request.storyPoints());
        }
        story.setUpdatedBy(actorId);
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }
}