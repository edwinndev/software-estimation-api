package com.intecx.inscope.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToggleUserStatusRequest {

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean isActive;
}
