package com.intecx.inscope.dto.request.project;

import com.intecx.inscope.enumeration.ProjectStatus;
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
public class UpdateProjectStatusRequest {

    @NotNull(message = "El estado del proyecto es obligatorio")
    private ProjectStatus status;
}
