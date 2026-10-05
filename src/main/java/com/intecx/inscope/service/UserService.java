package com.intecx.inscope.service;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.user.CreateUserRequest;
import com.intecx.inscope.dto.request.user.ToggleUserStatusRequest;
import com.intecx.inscope.dto.request.user.UpdateUserRequest;
import com.intecx.inscope.dto.response.MessageResponse;
import com.intecx.inscope.dto.response.UserResponse;
import java.util.UUID;

public interface UserService {
    PaginatedResponse<UserResponse> search(QueryRequest query);
    UserResponse getById(UUID id);
    UserResponse create(CreateUserRequest request, UUID actorId);
    UserResponse update(UUID id, UpdateUserRequest request, UUID actorId);
    UserResponse changeStatus(UUID id, ToggleUserStatusRequest request, UUID actorId);
    MessageResponse resendInvitation(UUID id, UUID actorId);
    void delete(UUID id, UUID actorId);
}
