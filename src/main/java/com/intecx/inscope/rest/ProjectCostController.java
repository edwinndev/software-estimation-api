package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.ProjectCostResponse;
import com.intecx.inscope.service.ProjectCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Costos de proyectos", description = "Endpoints para el cálculo y desglose de costos de un proyecto")
@RestController
@RequestMapping("/api/v1/project-costs")
@RequiredArgsConstructor
public class ProjectCostController {

    private final ProjectCostService projectCostService;

    @Operation(summary = "Consultar el costo base y el desglose por tarea y perfil de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Costo del proyecto obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{projectId}")
    @PreAuthorize("hasAuthority('cost:read')")
    public ResponseEntity<ProjectCostResponse> getCosts(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectCostService.getCosts(projectId));
    }
}
