package com.intecx.inscope.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        boolean isActive,
        UUID roleId,
        String roleCode,
        String roleName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

