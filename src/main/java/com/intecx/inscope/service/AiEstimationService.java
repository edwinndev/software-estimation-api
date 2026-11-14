package com.intecx.inscope.service;

import com.intecx.inscope.dto.ai.AiAllocationResponse;
import com.intecx.inscope.dto.ai.AiRiskPredictionResponse;
import java.util.Map;
import java.util.UUID;

public interface AiEstimationService {

    AiRiskPredictionResponse predictRisk(UUID projectId);

    AiAllocationResponse optimizeAllocation(UUID projectId, String algorithm, Double maxHoursPerProfile);

    Map<String, Object> getModelMetrics();
}

