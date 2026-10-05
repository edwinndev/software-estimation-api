package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.response.RoleResponse;
import com.intecx.inscope.entity.Role;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.RoleMapper;
import com.intecx.inscope.repository.RoleRepository;
import com.intecx.inscope.service.RoleService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllActive() {
        return roleRepository.findAllActive().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        return roleMapper.toResponse(role);
    }
}
