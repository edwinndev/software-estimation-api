package com.intecx.inscope.dto.request.profile;

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
public class ToggleProfileStatusRequest {

    @NotNull(message = "El estado del perfil es obligatorio")
    private Boolean isActive;
}
