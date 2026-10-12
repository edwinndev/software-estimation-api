package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.project.ProjectContingencyRequest;
import com.intecx.inscope.dto.response.project.ProjectContingencyResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.repository.ProjectRepository;
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

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public ProjectContingencyResponse calculate(UUID projectId, ProjectContingencyRequest request) {
        Project project = projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        BigDecimal margin = marginOf(project);
        BigDecimal factor = BigDecimal.ONE.add(
                margin.divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP));

        return ProjectContingencyResponse.builder()
                .riskLevel(project.getRiskLevel())
                .contingencyMargin(margin)
                .estimatedTime(request.estimatedTime())
                .estimatedTimeWithContingency(apply(request.estimatedTime(), factor))
                .estimatedCost(request.estimatedCost())
                .estimatedCostWithContingency(apply(request.estimatedCost(), factor))
                .build();
    }

    private BigDecimal marginOf(Project project) {
        return project.getContingencyMargin() == null
                ? BigDecimal.ZERO
                : project.getContingencyMargin();
    }

    private BigDecimal apply(BigDecimal base, BigDecimal factor) {
        return base.multiply(factor).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
