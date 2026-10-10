package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.response.StoryTaskHoursResponse;
import com.intecx.inscope.dto.response.TaskHoursResponse;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TaskAssignment;
import com.intecx.inscope.entity.TaskHourAdjustment;
import com.intecx.inscope.entity.TaskProfileId;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TaskHoursMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.TaskAssignmentRepository;
import com.intecx.inscope.repository.TaskHourAdjustmentRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.service.TaskHoursService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskHoursServiceImpl implements TaskHoursService {

    private static final int HOURS_SCALE = 2;
    private static final String ORIGIN_AUTOMATIC = "Reparto automático";
    private static final String ORIGIN_MANUAL = "Ajuste manual";
    private static final String UNKNOWN_PROFILE = "Perfil no disponible";

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskHourAdjustmentRepository taskHourAdjustmentRepository;
    private final TechnicalProfileRepository technicalProfileRepository;
    private final TaskHoursMapper taskHoursMapper;

    @Override
    @Transactional(readOnly = true)
    public TaskHoursResponse getTaskHours(UUID projectId) {
        projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        List<Task> tasks = taskRepository.findActiveByProjectId(projectId);
        if (tasks.isEmpty()) {
            return taskHoursMapper.toResponse(List.of(), BigDecimal.ZERO);
        }

        List<UUID> taskIds = tasks.stream().map(Task::getId).toList();
        List<TaskAssignment> assignments = taskAssignmentRepository.findByIdTaskIdIn(taskIds);
        Map<UUID, List<TaskAssignment>> assignmentsByTask = assignments.stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getId().getTaskId()));
        Map<TaskProfileId, TaskHourAdjustment> adjustments = taskHourAdjustmentRepository.findByIdTaskIdIn(taskIds)
                .stream()
                .collect(Collectors.toMap(TaskHourAdjustment::getId, Function.identity()));
        Map<UUID, String> profileNames = findProfileNames(assignments);

        List<StoryTaskHoursResponse> stories = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getStory().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(storyTasks -> buildStory(storyTasks, assignmentsByTask, adjustments, profileNames))
                .filter(story -> !story.getRows().isEmpty())
                .toList();

        return taskHoursMapper.toResponse(stories, sum(stories.stream().map(StoryTaskHoursResponse::getTotalHours)));
    }

    private Map<UUID, String> findProfileNames(List<TaskAssignment> assignments) {
        Set<UUID> profileIds = assignments.stream()
                .map(assignment -> assignment.getId().getTechnicalProfileId())
                .collect(Collectors.toSet());
        return technicalProfileRepository.findAllById(profileIds).stream()
                .collect(Collectors.toMap(TechnicalProfile::getId, TechnicalProfile::getName));
    }

    private StoryTaskHoursResponse buildStory(
            List<Task> storyTasks,
            Map<UUID, List<TaskAssignment>> assignmentsByTask,
            Map<TaskProfileId, TaskHourAdjustment> adjustments,
            Map<UUID, String> profileNames
    ) {
        List<TaskProfileHoursResponse> rows = storyTasks.stream()
                .flatMap(task -> buildRows(task, assignmentsByTask.getOrDefault(task.getId(), List.of()),
                        adjustments, profileNames).stream())
                .toList();
        return taskHoursMapper.toStoryResponse(
                storyTasks.get(0).getStory(),
                rows,
                sum(rows.stream().map(TaskProfileHoursResponse::getHours))
        );
    }

    private List<TaskProfileHoursResponse> buildRows(
            Task task,
            List<TaskAssignment> taskAssignments,
            Map<TaskProfileId, TaskHourAdjustment> adjustments,
            Map<UUID, String> profileNames
    ) {
        if (taskAssignments.isEmpty()) {
            return List.of();
        }
        BigDecimal share = BigDecimal.valueOf(task.getEstimateHours())
                .divide(BigDecimal.valueOf(taskAssignments.size()), HOURS_SCALE, RoundingMode.HALF_UP);
        return taskAssignments.stream()
                .map(assignment -> buildRow(task, assignment.getId(), share, adjustments.get(assignment.getId()), profileNames))
                .sorted(Comparator.comparing(TaskProfileHoursResponse::getProfileName))
                .toList();
    }

    private TaskProfileHoursResponse buildRow(
            Task task,
            TaskProfileId id,
            BigDecimal share,
            TaskHourAdjustment adjustment,
            Map<UUID, String> profileNames
    ) {
        UUID profileId = id.getTechnicalProfileId();
        return taskHoursMapper.toRowResponse(
                task,
                profileId,
                profileNames.getOrDefault(profileId, UNKNOWN_PROFILE),
                adjustment == null ? share : adjustment.getHours(),
                originOf(adjustment)
        );
    }

    private String originOf(TaskHourAdjustment adjustment) {
        if (adjustment == null) {
            return ORIGIN_AUTOMATIC;
        }
        return adjustment.getReason().isBlank() ? ORIGIN_MANUAL : adjustment.getReason();
    }

    private BigDecimal sum(Stream<BigDecimal> values) {
        return values.reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}