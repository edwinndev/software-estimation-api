package com.intecx.inscope.service;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.project.CreateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectRequest;
import com.intecx.inscope.dto.request.project.UpdateProjectStatusRequest;
import com.intecx.inscope.dto.response.ProjectResponse;
import com.intecx.inscope.dto.response.ProjectStatusHistoryResponse;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    PaginatedResponse<ProjectResponse> search(QueryRequest query);

    ProjectResponse create(CreateProjectRequest request, UUID actorId);

    ProjectResponse update(UUID id, UpdateProjectRequest request, UUID actorId);

    ProjectResponse updateStatus(UUID id, UpdateProjectStatusRequest request, UUID actorId);

    List<ProjectStatusHistoryResponse> getStatusHistory(UUID id);

    void delete(UUID id, UUID actorId);

    void deleteEstimation(UUID projectId, UUID estimationId);
}
