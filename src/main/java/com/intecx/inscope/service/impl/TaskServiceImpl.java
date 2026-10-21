package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryFields;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.common.QuerySupport;
import com.intecx.inscope.dto.request.task.CreateTaskRequest;
import com.intecx.inscope.dto.request.task.UpdateTaskRequest;
import com.intecx.inscope.dto.response.TaskResponse;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.exception.BadRequestException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TaskMapper;
import com.intecx.inscope.mapper.TechnicalProfileMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.TaskService;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final QueryFields TASK_FIELDS = QueryFields.sortingBy("position")
            .filter("search", "title", "description")
            .filter("status", "status")
            .sortable("title", "status", "position", "createdAt");

    private final TaskRepository taskRepository;
    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final TechnicalProfileRepository technicalProfileRepository;
    private final TaskMapper taskMapper;
    private final TechnicalProfileMapper technicalProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> searchByStory(UUID storyId, QueryRequest query) {
        requireActiveStory(storyId);
        Specification<Task> scope = (root, criteria, builder) -> builder.and(
                builder.equal(root.get("story").get("id"), storyId),
                builder.isNull(root.get("deletedAt")),
                builder.isNull(root.get("story").get("deletedAt"))
        );
        return search(scope, query);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> searchByProject(UUID projectId, QueryRequest query) {
        requireActiveProject(projectId);
        Specification<Task> scope = (root, criteria, builder) -> builder.and(
                builder.equal(root.get("story").get("project").get("id"), projectId),
                builder.isNull(root.get("deletedAt")),
                builder.isNull(root.get("story").get("deletedAt")),
                builder.isNull(root.get("story").get("project").get("deletedAt"))
        );
        return search(scope, query);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findById(UUID id) {
        return taskMapper.toResponse(requireActiveTask(id));
    }

    @Override
    @Transactional
    public TaskResponse create(UUID storyId, CreateTaskRequest request, UUID actorId) {
        UserStory story = requireActiveStory(storyId);
        int position = taskRepository.findLastPositionByStoryId(storyId) + 1;
        Task task = taskMapper.toEntity(request, story, position, actorId);
        if (request.getResponsibleProfileIds() != null) {
            task.setResponsibleProfiles(new LinkedHashSet<>(resolveProfiles(request.getResponsibleProfileIds())));
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request, UUID actorId) {
        Task task = requireActiveTask(id);
        taskMapper.updateEntity(task, request, actorId);
        if (request.getResponsibleProfileIds() != null) {
            task.setResponsibleProfiles(new LinkedHashSet<>(resolveProfiles(request.getResponsibleProfileIds())));
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicalProfileResponse> getResponsibles(UUID taskId) {
        return requireActiveTask(taskId).getResponsibleProfiles().stream()
                .sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .map(technicalProfileMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse updateResponsibles(UUID taskId, List<UUID> profileIds, UUID actorId) {
        if (profileIds == null) {
            throw new BadRequestException("La lista de responsables es obligatoria");
        }
        Task task = requireActiveTask(taskId);
        task.setResponsibleProfiles(new LinkedHashSet<>(resolveProfiles(profileIds)));
        task.setUpdatedBy(actorId);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID actorId) {
        Task task = requireActiveTask(id);
        task.setDeletedAt(LocalDateTime.now());
        task.setDeletedBy(actorId);
        taskRepository.save(task);
    }

    private PaginatedResponse<TaskResponse> search(Specification<Task> scope, QueryRequest query) {
        QueryRequest searchQuery = query == null ? new QueryRequest(null, null) : query;
        return QuerySupport.search(
                taskRepository,
                scope,
                TASK_FIELDS,
                searchQuery,
                taskMapper::toResponse,
                "taskResponse"
        );
    }

    private List<TechnicalProfile> resolveProfiles(List<UUID> profileIds) {
        if (profileIds.stream().anyMatch(id -> id == null)) {
            throw new BadRequestException("Los identificadores de responsables no pueden ser nulos");
        }
        Set<UUID> uniqueIds = new LinkedHashSet<>(profileIds);
        if (uniqueIds.size() != profileIds.size()) {
            throw new BadRequestException("La lista contiene perfiles técnicos duplicados");
        }
        if (uniqueIds.isEmpty()) {
            return List.of();
        }

        List<TechnicalProfile> profiles = technicalProfileRepository.findByIdInAndDeletedAtIsNullAndIsActiveTrue(uniqueIds);
        if (profiles.size() != uniqueIds.size()) {
            throw new ResourceNotFoundException("Uno o más perfiles técnicos no existen o están inactivos");
        }
        return profiles;
    }

    private Task requireActiveTask(UUID id) {
        return taskRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    private UserStory requireActiveStory(UUID id) {
        UserStory story = userStoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia de usuario no encontrada"));
        if (story.getProject().getDeletedAt() != null) {
            throw new ResourceNotFoundException("Historia de usuario no encontrada");
        }
        return story;
    }

    private Project requireActiveProject(UUID id) {
        return projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
    }
}