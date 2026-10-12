package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.response.SprintEstimationResponse;
import com.intecx.inscope.dto.response.TaskHoursResponse;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import com.intecx.inscope.dto.response.TimeEstimationResponse;
import com.intecx.inscope.entity.SprintConfig;
import com.intecx.inscope.mapper.TimeEstimationMapper;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.SprintEstimationService;
import com.intecx.inscope.service.TaskHoursService;
import com.intecx.inscope.service.TimeEstimationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeEstimationServiceImpl implements TimeEstimationService {

    private final UserStoryRepository userStoryRepository;
    private final TaskRepository taskRepository;
    private final SprintConfigRepository sprintConfigRepository;
    private final SprintEstimationService sprintEstimationService;
    private final TaskHoursService taskHoursService;
    private final TimeEstimationMapper timeEstimationMapper;

    @Override
    @Transactional(readOnly = true)
    public TimeEstimationResponse estimateTime(UUID projectId) {
        SprintEstimationResponse sprints = sprintEstimationService.estimateSprints(projectId);
        SprintConfig config = sprintConfigRepository.findById(projectId)
                .orElseGet(() -> SprintConfig.withDefaults(projectId));
        TaskHoursResponse taskHours = taskHoursService.getTaskHours(projectId);

        return timeEstimationMapper.toResponse(
                sprints,
                config,
                userStoryRepository.countActiveWithStoryPointsByProjectId(projectId),
                userStoryRepository.countActiveByProjectId(projectId),
                countTasksWithHours(taskHours),
                taskRepository.countActiveByProjectId(projectId),
                sprints.getEstimatedSprints() * config.getDuration(),
                taskHours.getTotalHours()
        );
    }

    private long countTasksWithHours(TaskHoursResponse taskHours) {
        return taskHours.getStories().stream()
                .flatMap(story -> story.getRows().stream())
                .map(TaskProfileHoursResponse::getTaskId)
                .distinct()
                .count();
    }
}