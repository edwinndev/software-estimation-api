package com.intecx.inscope.service;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.task.CreateTaskRequest;
import com.intecx.inscope.dto.request.task.UpdateTaskRequest;
import com.intecx.inscope.dto.response.TaskResponse;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import java.util.List;
import java.util.UUID;

public interface TaskService {

    PaginatedResponse<TaskResponse> searchByStory(UUID storyId, QueryRequest query);

    PaginatedResponse<TaskResponse> searchByProject(UUID projectId, QueryRequest query);

    TaskResponse findById(UUID id);

    TaskResponse create(UUID storyId, CreateTaskRequest request, UUID actorId);

    TaskResponse update(UUID id, UpdateTaskRequest request, UUID actorId);

    List<TechnicalProfileResponse> getResponsibles(UUID taskId);

    TaskResponse updateResponsibles(UUID taskId, List<UUID> profileIds, UUID actorId);

    void delete(UUID id, UUID actorId);
}