package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryFields;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.common.QuerySupport;
import com.intecx.inscope.dto.request.project.CreateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectStatusRequest;
import com.intecx.inscope.dto.response.ProjectResponse;
import com.intecx.inscope.dto.response.ProjectStatusHistoryResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.ProjectStatusHistory;
import com.intecx.inscope.entity.User;
import com.intecx.inscope.enumeration.ProjectStatus;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.ProjectMapper;
import com.intecx.inscope.repository.EstimationSnapshotRepository;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.ProjectStatusHistoryRepository;
import com.intecx.inscope.repository.UserRepository;
import com.intecx.inscope.service.ProjectService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final QueryFields PROJECT_FIELDS = QueryFields.sortingBy("createdAt")
            .filter("search", "name", "description", "type")
            .filter("name", "name")
            .filter("type", "type")
            .filter("status", "status")
            .filter("startDate", "startDate")
            .filter("plannedDate", "plannedDate")
            .filter("responsibleId", "responsible.id")
            .filter("createdAt", "createdAt")
            .sortable("name", "type", "status", "startDate", "plannedDate");

    private final ProjectRepository projectRepository;
    private final EstimationSnapshotRepository estimationSnapshotRepository;
    private final ProjectStatusHistoryRepository projectStatusHistoryRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponse> search(QueryRequest query) {
        Specification<Project> notDeletedScope = (root, q, cb) -> cb.isNull(root.get("deletedAt"));
        QueryRequest searchQuery = query == null ? new QueryRequest(null, null) : query;
        return QuerySupport.searchWithBatchMapping(
                projectRepository,
                notDeletedScope,
                PROJECT_FIELDS,
                searchQuery,
                this::toResponsesWithEstimations,
                "projectResponse"
        );
    }

    @Override
    @Transactional
    public ProjectResponse create(CreateProjectRequest request, UUID actorId) {
        User responsible = userRepository.findActiveById(request.getResponsibleId())
                .orElseThrow(() -> new ResourceNotFoundException("El responsable especificado no existe"));

        Project project = projectMapper.toEntity(request, responsible, actorId);
        Project savedProject = projectRepository.save(project);
        recordStatusChange(savedProject, null, savedProject.getStatus(), actorId);
        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request, UUID actorId) {
        Project project = projectRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new ConflictException("Solo se pueden actualizar proyectos en estado Borrador");
        }

        User responsible = userRepository.findActiveById(request.getResponsibleId())
                .orElseThrow(() -> new ResourceNotFoundException("El responsable especificado no existe"));
        projectMapper.updateEntity(project, request, responsible, actorId);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse updateStatus(UUID id, UpdateProjectStatusRequest request, UUID actorId) {
        Project project = projectRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        ProjectStatus currentStatus = project.getStatus();
        ProjectStatus nextStatus = request.getStatus();
        if (!currentStatus.canTransitionTo(nextStatus)) {
            throw new ConflictException(
                    "No se permite la transición del proyecto de %s a %s".formatted(currentStatus, nextStatus)
            );
        }

        project.setStatus(nextStatus);
        project.setUpdatedBy(actorId);
        Project savedProject = projectRepository.save(project);
        recordStatusChange(savedProject, currentStatus, nextStatus, actorId);
        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectStatusHistoryResponse> getStatusHistory(UUID id) {
        projectRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        return projectStatusHistoryRepository.findByProjectIdOrderByChangedAtDesc(id).stream()
                .map(projectMapper::toStatusHistoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID actorId) {
        Project project = projectRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        requireDeletableStatus(project.getStatus());

        project.setDeletedAt(LocalDateTime.now());
        project.setDeletedBy(actorId);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteEstimation(UUID projectId, UUID estimationId) {
        Project project = projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        requireDeletableStatus(project.getStatus());

        var estimation = estimationSnapshotRepository.findActiveByIdAndProjectId(estimationId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Estimación no encontrada"));
        estimationSnapshotRepository.delete(estimation);
    }

    private List<ProjectResponse> toResponsesWithEstimations(List<Project> projects) {
        if (projects.isEmpty()) {
            return List.of();
        }

        List<UUID> projectIds = projects.stream().map(Project::getId).toList();
        return projectMapper.toResponses(
                projects,
                estimationSnapshotRepository.findSummariesByProjectIds(projectIds)
        );
    }

    private void requireDeletableStatus(ProjectStatus status) {
        if (status != ProjectStatus.DRAFT && status != ProjectStatus.REJECTED) {
            throw new ConflictException(
                    "El proyecto debe estar en estado Borrador o Rechazado para eliminarlo o eliminar sus estimaciones"
            );
        }
    }

    private void recordStatusChange(Project project, ProjectStatus previousStatus, ProjectStatus newStatus, UUID actorId) {
        ProjectStatusHistory history = ProjectStatusHistory.builder()
                .project(project)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changedBy(userRepository.getReferenceById(actorId))
                .changedAt(LocalDateTime.now())
                .build();
        projectStatusHistoryRepository.save(history);
    }
}
