package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.PermissionResponse;
import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAll();
}
