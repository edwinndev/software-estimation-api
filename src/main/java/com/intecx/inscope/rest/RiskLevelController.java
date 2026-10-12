package com.intecx.inscope.rest;

import com.intecx.inscope.dto.request.project.ContingencyConfigRequest;
import com.intecx.inscope.dto.response.RiskLevelConfigResponse;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.service.RiskLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Niveles de riesgo", description = "Configuración de contingencia por nivel de riesgo")
@RestController
@RequestMapping("/api/v1/risk-levels")
@RequiredArgsConstructor
public class RiskLevelController {

    private final RiskLevelService riskLevelService;

    @Operation(summary = "Obtener la configuración de contingencia de un nivel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración obtenida"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada")
    })
    @GetMapping("/{level}")
    @PreAuthorize("hasAuthority('project:read')")
    public ResponseEntity<RiskLevelConfigResponse> getConfig(@PathVariable RiskLevel level) {
        return ResponseEntity.ok(riskLevelService.getConfig(level));
    }

    @Operation(summary = "Actualizar el porcentaje de contingencia de un nivel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración actualizada"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada"),
            @ApiResponse(responseCode = "400", description = "Porcentaje inválido")
    })
    @PutMapping("/{level}")
    @PreAuthorize("hasAuthority('project:write')")
    public ResponseEntity<RiskLevelConfigResponse> updateConfig(
            @PathVariable RiskLevel level,
            @Valid @RequestBody ContingencyConfigRequest request
    ) {
        return ResponseEntity.ok(riskLevelService.updateConfig(level, request));
    }
}
