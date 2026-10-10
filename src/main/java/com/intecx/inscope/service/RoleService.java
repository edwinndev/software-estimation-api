package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.RoleResponse;
import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleResponse> getAll();
    List<RoleResponse> getAllActive();
    RoleResponse getById(UUID id);
}
