package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.project.ProjectContingencyRequest;
import com.intecx.inscope.dto.response.ProjectContingencyResponse;
import com.intecx.inscope.dto.response.ProjectCostResponse;
import com.intecx.inscope.dto.response.ProjectEstimationResponse;
import com.intecx.inscope.dto.response.ReportSnapshotDetailResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.ReportSnapshot;
import com.intecx.inscope.entity.RiskConfig;
import com.intecx.inscope.entity.SprintConfig;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.User;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.enumeration.SprintUnit;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ReportSnapshotMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.ReportSnapshotRepository;
import com.intecx.inscope.repository.RiskConfigRepository;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.ProjectCostService;
import com.intecx.inscope.service.ProjectEstimationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectEstimationServiceImpl implements ProjectEstimationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;
    private static final int DEFAULT_VELOCITY = 5;
    private static final int DEFAULT_DURATION = 2;
    private static final SprintUnit DEFAULT_UNIT = SprintUnit.DAYS;
    private static final RiskLevel DEFAULT_RISK = RiskLevel.MEDIUM;
    private static final BigDecimal DEFAULT_MARGIN = BigDecimal.valueOf(15);
    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final String ESTIMATION_NOT_FOUND = "Estimación no encontrada";
    private static final String CONFIG_NOT_FOUND = "Configuración de riesgo no encontrada";

    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final TaskRepository taskRepository;
    private final SprintConfigRepository sprintConfigRepository;
    private final RiskConfigRepository riskConfigRepository;
    private final ReportSnapshotRepository reportSnapshotRepository;
    private final ProjectCostService projectCostService;
    private final ReportSnapshotMapper reportSnapshotMapper;

    @Override
    @Transactional(readOnly = true)
    public ReportSnapshotDetailResponse consolidate(UUID projectId) {
        Project project = requireProject(projectId);
        ReportSnapshot snapshot = buildSnapshot(project, null);
        return reportSnapshotMapper.toDetailResponse(snapshot);
    }

    @Override
    @Transactional
    public ReportSnapshotDetailResponse create(UUID projectId, UUID actorId) {
        Project project = requireProject(projectId);
        ReportSnapshot snapshot = reportSnapshotRepository.save(buildSnapshot(project, actorId));
        return reportSnapshotMapper.toDetailResponse(snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectEstimationResponse> listByProject(UUID projectId) {
        requireProject(projectId);
        return reportSnapshotMapper.toResponseList(
                reportSnapshotRepository.findByProjectIds(List.of(projectId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSnapshotDetailResponse getById(UUID projectId, UUID estimationId) {
        ReportSnapshot snapshot = reportSnapshotRepository
                .findByIdAndProject_IdAndProject_DeletedAtIsNull(estimationId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(ESTIMATION_NOT_FOUND));
        return reportSnapshotMapper.toDetailResponse(snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectContingencyResponse calculate(UUID projectId, ProjectContingencyRequest request) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        RiskConfig config = riskConfigRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIG_NOT_FOUND));

        return applyContingency(config.getLevel(), config.getContingencyMargin(),
                request.getEstimatedTime(), request.getEstimatedCost());
    }

    private ReportSnapshot buildSnapshot(Project project, UUID actorId) {
        List<UserStory> stories = userStoryRepository.findByProject_IdAndDeletedAtIsNull(project.getId());
        List<Task> tasks = taskRepository.findByStory_Project_IdAndDeletedAtIsNull(project.getId());
        SprintConfig sprint = resolveSprintConfig(project);
        RiskConfig risk = resolveRiskConfig(project);
        ProjectCostResponse costs = projectCostService.getCosts(project.getId());

        int storiesTotal = stories.size();
        int storiesWithPoints = (int) stories.stream().filter(story -> story.getStoryPoints() != null).count();
        int baseEffortPoints = stories.stream()
                .filter(story -> story.getStoryPoints() != null)
                .mapToInt(story -> story.getStoryPoints())
                .sum();
        int tasksTotal = tasks.size();
        int tasksWithHours = (int) tasks.stream().filter(task -> task.getEstimateHours() != null && task.getEstimateHours() > 0).count();
        BigDecimal totalEffortHours = tasks.stream()
                .map(task -> BigDecimal.valueOf(task.getEstimateHours() == null ? 0 : task.getEstimateHours()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);

        int velocity = sprint.getVelocity() == null || sprint.getVelocity() <= 0
                ? DEFAULT_VELOCITY
                : sprint.getVelocity();
        int duration = sprint.getDuration() == null || sprint.getDuration() <= 0
                ? DEFAULT_DURATION
                : sprint.getDuration();
        SprintUnit unit = sprint.getUnit() == null ? DEFAULT_UNIT : sprint.getUnit();
        int totalSprints = baseEffortPoints == 0
                ? 0
                : (int) Math.ceil(baseEffortPoints / (double) velocity);
        BigDecimal baseTime = BigDecimal.valueOf((long) totalSprints * duration)
                .setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal baseCost = costs.total() == null || costs.total().amount() == null
                ? BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP)
                : costs.total().amount().setScale(SCALE, RoundingMode.HALF_UP);

        ProjectContingencyResponse contingency = applyContingency(
                risk.getLevel(), risk.getContingencyMargin(), baseTime, baseCost);

        User owner = project.getResponsible();
        String ownerName = owner == null
                ? ""
                : ("%s %s".formatted(owner.getFirstName(), owner.getLastName())).trim();

        return ReportSnapshot.builder()
                .project(project)
                .projectName(project.getName())
                .projectStatus(project.getStatus())
                .projectType(project.getType())
                .projectOwner(ownerName)
                .baseEffortPoints(baseEffortPoints)
                .totalSprints(totalSprints)
                .totalEffortHours(totalEffortHours)
                .storiesTotal(storiesTotal)
                .storiesWithPoints(storiesWithPoints)
                .tasksTotal(tasksTotal)
                .tasksWithHours(tasksWithHours)
                .baseTime(contingency.baseTime())
                .timeUnit(unit)
                .baseCost(contingency.baseCost())
                .riskLevel(contingency.riskLevel())
                .contingencyMarginPercentage(contingency.contingencyMarginPercentage())
                .contingencyTime(contingency.contingencyTime())
                .contingencyCost(contingency.contingencyCost())
                .totalTime(contingency.totalTime())
                .totalCost(contingency.totalCost())
                .createdAt(LocalDateTime.now())
                .createdBy(actorId)
                .build();
    }

    private ProjectContingencyResponse applyContingency(
            RiskLevel level,
            BigDecimal margin,
            BigDecimal baseTime,
            BigDecimal baseCost
    ) {
        BigDecimal safeMargin = margin == null ? DEFAULT_MARGIN : margin;
        BigDecimal factor = BigDecimal.ONE.add(safeMargin.divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP));
        BigDecimal contingencyTime = baseTime.multiply(safeMargin).divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal totalTime = baseTime.multiply(factor).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal contingencyCost = baseCost.multiply(safeMargin).divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal totalCost = baseCost.multiply(factor).setScale(SCALE, RoundingMode.HALF_UP);

        return ProjectContingencyResponse.builder()
                .riskLevel(level == null ? DEFAULT_RISK : level)
                .contingencyMarginPercentage(safeMargin)
                .baseTime(baseTime.setScale(SCALE, RoundingMode.HALF_UP))
                .contingencyTime(contingencyTime)
                .totalTime(totalTime)
                .baseCost(baseCost.setScale(SCALE, RoundingMode.HALF_UP))
                .contingencyCost(contingencyCost)
                .totalCost(totalCost)
                .build();
    }

    private SprintConfig resolveSprintConfig(Project project) {
        return sprintConfigRepository.findByProjectId(project.getId())
                .orElseGet(() -> SprintConfig.builder()
                        .project(project)
                        .velocity(DEFAULT_VELOCITY)
                        .duration(DEFAULT_DURATION)
                        .unit(DEFAULT_UNIT)
                        .build());
    }

    private RiskConfig resolveRiskConfig(Project project) {
        return riskConfigRepository.findByProjectId(project.getId())
                .orElseGet(() -> RiskConfig.builder()
                        .project(project)
                        .level(DEFAULT_RISK)
                        .contingencyMargin(DEFAULT_MARGIN)
                        .build());
    }

    private Project requireProject(UUID projectId) {
        return projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));
    }
}
