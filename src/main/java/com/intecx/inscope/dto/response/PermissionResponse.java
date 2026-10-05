package com.intecx.inscope.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record PermissionResponse(
        UUID id,
        String code,
        String label,
        String groupCode,
        String groupLabel
) {
}
