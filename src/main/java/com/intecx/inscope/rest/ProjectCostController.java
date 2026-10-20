package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.ProjectCostResponse;
import com.intecx.inscope.service.ProjectCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project-costs")
@RequiredArgsConstructor
@Tag(name = "Costos de proyectos", description = "Cálculo y desglose de costos con datos actuales")
public class ProjectCostController {

    private final ProjectCostService projectCostService;

    @GetMapping("/{projectId}")
    @PreAuthorize("hasAuthority('cost:read')")
    @Operation(summary = "Consultar el costo base y el desglose por tarea y perfil de un proyecto")
    public ProjectCostResponse getCosts(@PathVariable UUID projectId) {
        return projectCostService.getCosts(projectId);
    }
}
