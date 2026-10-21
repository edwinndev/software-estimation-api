package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryFields;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.common.QuerySupport;
import com.intecx.inscope.dto.request.userstory.CreateUserStoryRequest;
import com.intecx.inscope.dto.request.userstory.UpdateUserStoryRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.UserStory;
import com.intecx.inscope.exception.ConflictException;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.UserStoryMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.UserStoryService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStoryServiceImpl implements UserStoryService {

    private static final QueryFields STORY_FIELDS = QueryFields.sortingBy("position")
            .filter("search", "code", "title", "description")
            .filter("code", "code")
            .filter("status", "status")
            .filter("priority", "priority")
            .sortable("code", "title", "status", "priority", "position", "createdAt");

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserStoryMapper userStoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserStoryResponse> searchByProject(UUID projectId, QueryRequest query) {
        requireActiveProject(projectId);
        Specification<UserStory> scope = (root, criteria, builder) -> builder.and(
                builder.equal(root.get("project").get("id"), projectId),
                builder.isNull(root.get("deletedAt"))
        );
        QueryRequest searchQuery = query == null ? new QueryRequest(null, null) : query;
        return QuerySupport.search(
                userStoryRepository,
                scope,
                STORY_FIELDS,
                searchQuery,
                userStoryMapper::toResponse,
                "userStoryResponse"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserStoryResponse findById(UUID id) {
        return userStoryMapper.toResponse(requireActiveStory(id));
    }

    @Override
    @Transactional
    public UserStoryResponse create(UUID projectId, CreateUserStoryRequest request, UUID actorId) {
        Project project = requireActiveProject(projectId);
        String code = request.getCode().trim().toUpperCase();
        if (userStoryRepository.existsByProject_IdAndCode(projectId, code)) {
            throw new ConflictException("Ya existe una historia con ese código en el proyecto");
        }

        int position = userStoryRepository.findLastPositionByProjectId(projectId) + 1;
        UserStory story = userStoryMapper.toEntity(request, project, position, actorId);
        return userStoryMapper.toResponse(userStoryRepository.save(story));
    }

    @Override
    @Transactional
    public UserStoryResponse update(UUID id, UpdateUserStoryRequest request, UUID actorId) {
        UserStory story = requireActiveStory(id);
        String code = request.getCode().trim().toUpperCase();
        if (!story.getCode().equalsIgnoreCase(code)
                && userStoryRepository.existsByProject_IdAndCodeAndIdNot(story.getProject().getId(), code, id)) {
            throw new ConflictException("Ya existe una historia con ese código en el proyecto");
        }

        userStoryMapper.updateEntity(story, request, actorId);
        return userStoryMapper.toResponse(userStoryRepository.save(story));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID actorId) {
        UserStory story = requireActiveStory(id);
        if (taskRepository.existsByStory_IdAndDeletedAtIsNull(id)) {
            throw new ConflictException("No se puede eliminar la historia mientras tenga tareas activas");
        }

        story.setDeletedAt(LocalDateTime.now());
        story.setDeletedBy(actorId);
        userStoryRepository.save(story);
    }

    private UserStory requireActiveStory(UUID id) {
        return userStoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia de usuario no encontrada"));
    }

    private Project requireActiveProject(UUID id) {
        return projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
    }
}