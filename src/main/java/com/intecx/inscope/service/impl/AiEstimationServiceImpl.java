package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.ai.AiAllocationRequest;
import com.intecx.inscope.dto.ai.AiAllocationRequest.ProfilePayload;
import com.intecx.inscope.dto.ai.AiAllocationRequest.TaskPayload;
import com.intecx.inscope.dto.ai.AiAllocationResponse;
import com.intecx.inscope.dto.ai.AiRiskPredictionRequest;
import com.intecx.inscope.dto.ai.AiRiskPredictionResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.SprintConfig;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.enumeration.ExperienceLevel;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.AiEstimationService;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AiEstimationServiceImpl implements AiEstimationService {

    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final int DEFAULT_VELOCITY = 5;

    private final WebClient aiWebClient;
    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final TaskRepository taskRepository;
    private final SprintConfigRepository sprintConfigRepository;
    private final TechnicalProfileRepository technicalProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public AiRiskPredictionResponse predictRisk(UUID projectId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        List<UserStory> stories = userStoryRepository.findByProject_IdAndDeletedAtIsNull(projectId);
        List<Task> tasks = taskRepository.findByStory_Project_IdAndDeletedAtIsNull(projectId);
        SprintConfig sprint = sprintConfigRepository.findByProjectId(projectId).orElse(null);

        int baseEffortPoints = stories.stream()
                .filter(s -> s.getStoryPoints() != null)
                .mapToInt(UserStory::getStoryPoints)
                .sum();

        BigDecimal totalEffortHours = tasks.stream()
                .map(t -> BigDecimal.valueOf(t.getEstimateHours() == null ? 0 : t.getEstimateHours()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int velocity = (sprint != null && sprint.getVelocity() != null && sprint.getVelocity() > 0)
                ? sprint.getVelocity()
                : DEFAULT_VELOCITY;
        int totalSprints = baseEffortPoints == 0 ? 0 : (int) Math.ceil(baseEffortPoints / (double) velocity);
        long plannedDays = Math.max(1L, ChronoUnit.DAYS.between(project.getStartDate(), project.getPlannedDate()));

        Set<TechnicalProfile> assignedProfiles = tasks.stream()
                .flatMap(t -> t.getResponsibleProfiles().stream())
                .collect(Collectors.toSet());

        double juniorRatio = 0.0;
        double seniorRatio = 0.0;
        if (!assignedProfiles.isEmpty()) {
            long juniors = assignedProfiles.stream()
                    .filter(p -> p.getExperienceLevel() == ExperienceLevel.JUNIOR)
                    .count();
            long seniors = assignedProfiles.stream()
                    .filter(p -> p.getExperienceLevel() == ExperienceLevel.SENIOR
                            || p.getExperienceLevel() == ExperienceLevel.LEAD)
                    .count();
            juniorRatio = (double) juniors / assignedProfiles.size();
            seniorRatio = (double) seniors / assignedProfiles.size();
        }

        AiRiskPredictionRequest payload = new AiRiskPredictionRequest(
                project.getType(),
                baseEffortPoints,
                totalEffortHours,
                stories.size(),
                tasks.size(),
                totalSprints,
                plannedDays,
                juniorRatio,
                seniorRatio
        );

        return aiWebClient.post()
                .uri("/api/v1/risk/predict")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(AiRiskPredictionResponse.class)
                .block();
    }

    @Override
    @Transactional(readOnly = true)
    public AiAllocationResponse optimizeAllocation(UUID projectId, String algorithm, Double maxHoursPerProfile) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        List<TaskPayload> tasks = taskRepository.findByStory_Project_IdAndDeletedAtIsNull(projectId)
                .stream()
                .filter(t -> t.getEstimateHours() != null && t.getEstimateHours() > 0)
                .map(t -> new TaskPayload(t.getId().toString(), t.getTitle(), t.getEstimateHours()))
                .toList();

        if (tasks.isEmpty()) {
            throw new BadRequestException("El proyecto no tiene tareas con horas estimadas para optimizar");
        }

        List<ProfilePayload> profiles = technicalProfileRepository.findByDeletedAtIsNullAndIsActiveTrue()
                .stream()
                .map(p -> new ProfilePayload(
                        p.getId().toString(),
                        p.getName(),
                        p.getRole(),
                        p.getExperienceLevel(),
                        p.getHourlyRate()
                ))
                .toList();

        if (profiles.isEmpty()) {
            throw new BadRequestException("No hay perfiles técnicos activos disponibles");
        }

        AiAllocationRequest payload = new AiAllocationRequest(tasks, profiles, maxHoursPerProfile, algorithm);

        return aiWebClient.post()
                .uri("/api/v1/allocation/optimize")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(AiAllocationResponse.class)
                .block();
    }

    @Override
    public Map<String, Object> getModelMetrics() {
        return aiWebClient.get()
                .uri("/api/v1/models/metrics")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}

