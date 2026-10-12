package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.project.ContingencyConfigRequest;
import com.intecx.inscope.dto.request.project.RiskLevelUpdateRequest;
import com.intecx.inscope.dto.response.RiskLevelConfigResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.RiskLevelConfig;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.RiskLevelConfigMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.RiskLevelConfigRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskLevelService {

    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final String CONFIG_NOT_FOUND = "Configuración de riesgo no encontrada";

    private final RiskLevelConfigRepository configRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void updateProjectRiskLevel(UUID projectId, RiskLevelUpdateRequest request) {
        Project project = projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));
        RiskLevelConfig config = findConfigByLevel(request.riskLevel());
        project.setRiskLevel(request.riskLevel());
        project.setContingencyMargin(config.getContingencyPercentage());
    }

    @Transactional(readOnly = true)
    public RiskLevelConfigResponse getConfig(RiskLevel level) {
        return RiskLevelConfigMapper.toResponse(findConfigByLevel(level));
    }

    @Transactional
    public RiskLevelConfigResponse updateConfig(RiskLevel level, ContingencyConfigRequest request) {
        RiskLevelConfig config = findConfigByLevel(level);
        config.setContingencyPercentage(request.contingencyPercentage());
        return RiskLevelConfigMapper.toResponse(config);
    }

    private RiskLevelConfig findConfigByLevel(RiskLevel level) {
        return configRepository.findActiveByRiskLevel(level)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIG_NOT_FOUND));
    }
}
