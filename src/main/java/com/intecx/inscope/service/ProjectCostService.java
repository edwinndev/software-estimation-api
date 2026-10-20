package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.dto.response.ProjectCostResponse;
import com.intecx.inscope.dto.response.TaskCostResponse;
import com.intecx.inscope.dto.response.TaskProfileCostResponse;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ProjectCostMapper;
import com.intecx.inscope.repository.ProjectCostProjection;
import com.intecx.inscope.repository.ProjectCostRepository;
import com.intecx.inscope.repository.ProjectRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectCostService {

    private final ProjectRepository projectRepository;
    private final ProjectCostRepository projectCostRepository;
    private final CostCalculator costCalculator;
    private final CostAggregator costAggregator;
    private final ProfileCostAggregator profileCostAggregator;
    private final ProjectCostMapper projectCostMapper;
    private final TaskCostHoursResolver taskCostHoursResolver;

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ProjectCostResponse getCosts(UUID projectId) {
        projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        Map<UUID, List<ProjectCostProjection>> rowsByTask = projectCostRepository.findContributions(projectId).stream()
                .collect(Collectors.groupingBy(ProjectCostProjection::getTaskId, LinkedHashMap::new, Collectors.toList()));
        List<TaskCostResponse> tasks = rowsByTask.values().stream()
                .map(this::calculateTaskCost)
                .toList();
        if (tasks.isEmpty()) {
            return projectCostMapper.toResponse(projectId,
                    new CostAmountResponse(BigDecimal.ZERO, null), List.of(), List.of());
        }
        CostAmountResponse total = costAggregator.subtotal(tasks.stream().map(TaskCostResponse::getTotal).toList());
        List<ProfileCostContribution> contributions = tasks.stream()
                .flatMap(task -> task.getContributions().stream())
                .map(row -> new ProfileCostContribution(row.getTechnicalProfileId(), row.getCost()))
                .toList();
        return projectCostMapper.toResponse(projectId, total, tasks, profileCostAggregator.aggregate(contributions));
    }

    private TaskCostResponse calculateTaskCost(List<ProjectCostProjection> rows) {
        Map<UUID, BigDecimal> hours = taskCostHoursResolver.resolve(rows);
        Map<UUID, ProjectCostProjection> rowsByProfile = rows.stream()
                .collect(Collectors.toMap(ProjectCostProjection::getTechnicalProfileId, row -> row));
        List<TaskProfileCostResponse> contributions = hours.entrySet().stream()
                .map(entry -> calculateContribution(rowsByProfile.get(entry.getKey()), entry.getValue()))
                .toList();
        CostAmountResponse total = costAggregator.subtotal(
                contributions.stream().map(TaskProfileCostResponse::getCost).toList());
        return projectCostMapper.toTaskResponse(rows.getFirst().getTaskId(), total, contributions);
    }

    private TaskProfileCostResponse calculateContribution(ProjectCostProjection row, BigDecimal hours) {
        if (!Boolean.TRUE.equals(row.getProfileActive()) || row.getProfileDeletedAt() != null) {
            throw new ConflictException("La tarea %s tiene un perfil inactivo o eliminado".formatted(row.getTaskId()));
        }
        return projectCostMapper.toContributionResponse(row, hours, costCalculator.calculate(hours, row.getHourlyRate()));
    }
}
