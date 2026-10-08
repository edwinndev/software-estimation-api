package com.intecx.inscope.dto.request.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ToggleProfileStatusRequest(
        @NotNull(message = "El estado del perfil es obligatorio")
        @Schema(description = "Nuevo estado activo o inactivo del perfil", example = "true")
        Boolean isActive
) {
}
