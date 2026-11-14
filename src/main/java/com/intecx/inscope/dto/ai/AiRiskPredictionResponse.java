package com.intecx.inscope.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intecx.inscope.enumeration.RiskLevel;
import java.math.BigDecimal;
import java.util.Map;

public record AiRiskPredictionResponse(
        @JsonProperty("recommended_contingency_margin") BigDecimal recommendedContingencyMargin,
        @JsonProperty("predicted_risk_level") RiskLevel predictedRiskLevel,
        @JsonProperty("risk_probabilities") Map<String, Double> riskProbabilities,
        @JsonProperty("regression_model_used") String regressionModelUsed
) {
}

