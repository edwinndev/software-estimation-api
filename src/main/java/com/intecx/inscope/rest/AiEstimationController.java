package com.intecx.inscope.rest;

import com.intecx.inscope.dto.ai.AiAllocationResponse;
import com.intecx.inscope.dto.ai.AiRiskPredictionResponse;
import com.intecx.inscope.service.AiEstimationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "IA y Optimización", description = "Predicción de riesgo/contingencia y optimización de asignación vía microservicio Python")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AiEstimationController {

    private final AiEstimationService aiEstimationService;

    @Operation(summary = "Predecir margen de contingencia y nivel de riesgo del proyecto")
    @GetMapping("/projects/{projectId}/ai/risk-prediction")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<AiRiskPredictionResponse> predictRisk(@PathVariable UUID projectId) {
        return ResponseEntity.ok(aiEstimationService.predictRisk(projectId));
    }

    @Operation(summary = "Optimizar asignación de perfiles técnicos a tareas del proyecto")
    @PostMapping("/projects/{projectId}/ai/optimize-allocation")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<AiAllocationResponse> optimizeAllocation(
            @PathVariable UUID projectId,
            @RequestParam(defaultValue = "astar") String algorithm,
            @RequestParam(required = false) Double maxHoursPerProfile
    ) {
        return ResponseEntity.ok(aiEstimationService.optimizeAllocation(projectId, algorithm, maxHoursPerProfile));
    }

    @Operation(summary = "Consultar métricas e hiperparámetros de los modelos entrenados")
    @GetMapping("/ai/models/metrics")
    @PreAuthorize("hasAuthority('estimation:read')")
    public ResponseEntity<Map<String, Object>> getModelMetrics() {
        return ResponseEntity.ok(aiEstimationService.getModelMetrics());
    }
}

