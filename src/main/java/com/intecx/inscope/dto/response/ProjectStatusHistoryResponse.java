package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.ProjectStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProjectStatusHistoryResponse(
        UUID id,
        ProjectStatus previousStatus,
        ProjectStatus newStatus,
        LocalDateTime changedAt,
        UUID changedById,
        String changedByFirstName,
        String changedByLastName,
        String changedByEmail
) {
}
