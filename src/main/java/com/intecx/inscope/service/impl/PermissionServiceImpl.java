package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.response.PermissionResponse;
import com.intecx.inscope.mapper.PermissionMapper;
import com.intecx.inscope.repository.PermissionRepository;
import com.intecx.inscope.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .toList();
    }
}
