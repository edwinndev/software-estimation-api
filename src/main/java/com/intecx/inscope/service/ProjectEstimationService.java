package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.project.ProjectContingencyRequest;
import com.intecx.inscope.dto.response.project.ProjectContingencyResponse;
import com.intecx.inscope.entity.RiskConfig;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.RiskConfigRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectEstimationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;
    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final String CONFIG_NOT_FOUND = "Configuración de riesgo no encontrada";

    private final ProjectRepository projectRepository;
    private final RiskConfigRepository riskConfigRepository;

    @Transactional(readOnly = true)
    public ProjectContingencyResponse calculate(UUID projectId, ProjectContingencyRequest request) {
        projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        RiskConfig config = riskConfigRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIG_NOT_FOUND));

        BigDecimal margin = config.getContingencyMargin();
        BigDecimal factor = BigDecimal.ONE.add(
                margin.divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP));

        BigDecimal contingencyTime = request.estimatedTime()
                .multiply(margin).divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal totalTime = request.estimatedTime().multiply(factor)
                .setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal contingencyCost = request.estimatedCost()
                .multiply(margin).divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);
        BigDecimal totalCost = request.estimatedCost().multiply(factor)
                .setScale(SCALE, RoundingMode.HALF_UP);

        return ProjectContingencyResponse.builder()
                .riskLevel(config.getLevel())
                .contingencyMarginPercentage(margin)
                .baseTime(request.estimatedTime())
                .contingencyTime(contingencyTime)
                .totalTime(totalTime)
                .baseCost(request.estimatedCost())
                .contingencyCost(contingencyCost)
                .totalCost(totalCost)
                .build();
    }
}
