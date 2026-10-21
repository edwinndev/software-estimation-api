package com.intecx.inscope.dto.request.task;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
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
public class UpdateTaskResponsiblesRequest {

    @NotNull(message = "La lista de responsables es obligatoria")
    private List<UUID> responsibleProfileIds;
}
