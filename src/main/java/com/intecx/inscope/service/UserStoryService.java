package com.intecx.inscope.service;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.userstory.CreateUserStoryRequest;
import com.intecx.inscope.dto.request.userstory.UpdateUserStoryRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import java.util.UUID;

public interface UserStoryService {

    PaginatedResponse<UserStoryResponse> searchByProject(UUID projectId, QueryRequest query);

    UserStoryResponse findById(UUID id);

    UserStoryResponse create(UUID projectId, CreateUserStoryRequest request, UUID actorId);

    UserStoryResponse update(UUID id, UpdateUserStoryRequest request, UUID actorId);

    void delete(UUID id, UUID actorId);
}