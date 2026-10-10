package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.StoryTaskHoursResponse;
import com.intecx.inscope.dto.response.TaskHoursResponse;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.UserStory;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TaskHoursMapper {

    public TaskProfileHoursResponse toRowResponse(
            Task task, UUID profileId, String profileName, BigDecimal hours, String origin
    ) {
        return TaskProfileHoursResponse.builder()
                .taskId(task.getId())
                .taskTitle(task.getTitle())
                .technicalProfileId(profileId)
                .profileName(profileName)
                .hours(hours)
                .origin(origin)
                .build();
    }

    public StoryTaskHoursResponse toStoryResponse(
            UserStory story, List<TaskProfileHoursResponse> rows, BigDecimal totalHours
    ) {
        return StoryTaskHoursResponse.builder()
                .storyId(story.getId())
                .code(story.getCode())
                .title(story.getTitle())
                .totalHours(totalHours)
                .rows(rows)
                .build();
    }

    public TaskHoursResponse toResponse(List<StoryTaskHoursResponse> stories, BigDecimal totalHours) {
        return TaskHoursResponse.builder()
                .totalHours(totalHours)
                .stories(stories)
                .build();
    }
}