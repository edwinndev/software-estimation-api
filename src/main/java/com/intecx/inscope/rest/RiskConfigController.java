package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.project.RiskLevelUpdateRequest;
import com.intecx.inscope.dto.response.RiskConfigResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.RiskConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Riesgos", description = "Configuración de riesgo y contingencia por proyecto")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/risk-config")
@RequiredArgsConstructor
public class RiskConfigController {

    private final RiskConfigService riskConfigService;

    @Operation(summary = "Obtener la configuración de riesgo de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración obtenida"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('risk:read')")
    public ResponseEntity<RiskConfigResponse> get(@PathVariable UUID projectId) {
        return ResponseEntity.ok(riskConfigService.getByProject(projectId));
    }

    @Operation(summary = "Actualizar la configuración de riesgo de un proyecto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración actualizada"),
            @ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping
    @PreAuthorize("hasAuthority('risk:write')")
    public ResponseEntity<RiskConfigResponse> update(
            @PathVariable UUID projectId,
            @Valid @RequestBody RiskLevelUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(riskConfigService.update(projectId, request, principal.getId()));
    }
}
