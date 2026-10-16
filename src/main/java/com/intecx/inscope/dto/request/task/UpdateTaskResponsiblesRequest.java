package com.intecx.inscope.dto.request.task;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record UpdateTaskResponsiblesRequest(
        @NotNull(message = "La lista de responsables es obligatoria")
        List<UUID> responsibleProfileIds
) {
}