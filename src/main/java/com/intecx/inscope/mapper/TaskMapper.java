package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.request.task.CreateTaskRequest;
import com.intecx.inscope.dto.request.task.UpdateTaskRequest;
import com.intecx.inscope.dto.response.TaskResponse;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.TaskStatus;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        List<UUID> responsibleProfileIds = task.getResponsibleProfiles().stream()
                .map(TechnicalProfile::getId)
                .sorted(Comparator.naturalOrder())
                .toList();
        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getStory().getProject().getId())
                .userStoryId(task.getStory().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .estimateHours(task.getEstimateHours())
                .status(task.getStatus())
                .position(task.getPosition())
                .responsibleProfileIds(responsibleProfileIds)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public Task toEntity(CreateTaskRequest request, UserStory story, int position, UUID actorId) {
        return Task.builder()
                .story(story)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .estimateHours(request.getEstimateHours())
                .status(request.getStatus() == null ? TaskStatus.TODO : request.getStatus())
                .position(position)
                .createdBy(actorId)
                .build();
    }

    public void updateEntity(Task task, UpdateTaskRequest request, UUID actorId) {
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription().trim());
        task.setEstimateHours(request.getEstimateHours());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        task.setUpdatedBy(actorId);
    }
}
