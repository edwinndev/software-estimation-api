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
                .code(request.getCode().trim().toUpperCase())
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .acceptanceCriteria(normalizeOptional(request.getAcceptanceCriteria()))
                .priority(request.getPriority() == null ? StoryPriority.MEDIUM : request.getPriority())
                .status(StoryStatus.DRAFT)
                .storyPoints(request.getStoryPoints())
                .position(position)
                .createdBy(actorId)
                .build();
    }

    public void updateEntity(UserStory story, UpdateUserStoryRequest request, UUID actorId) {
        story.setCode(request.getCode().trim().toUpperCase());
        story.setTitle(request.getTitle().trim());
        story.setDescription(request.getDescription().trim());
        story.setAcceptanceCriteria(normalizeOptional(request.getAcceptanceCriteria()));
        story.setPriority(request.getPriority() == null ? StoryPriority.MEDIUM : request.getPriority());
        story.setStoryPoints(request.getStoryPoints());
        if (request.getStatus() != null) {
            story.setStatus(request.getStatus());
        }
        story.setUpdatedBy(actorId);
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }
}
