package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.RoleResponse;
import com.intecx.inscope.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper implements BaseMapper<Role, RoleResponse> {

    @Override
    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .isSystem(role.isSystem())
                .isActive(role.isActive())
                .build();
    }
}
