package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.PermissionResponse;
import com.intecx.inscope.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper implements BaseMapper<Permission, PermissionResponse> {

    @Override
    public PermissionResponse toResponse(Permission permission) {
        if (permission == null) {
            return null;
        }

        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .label(permission.getLabel())
                .groupCode(permission.getGroupCode())
                .groupLabel(permission.getGroupLabel())
                .build();
    }
}
