package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.project.RiskLevelUpdateRequest;
import com.intecx.inscope.dto.response.RiskConfigResponse;
import com.intecx.inscope.entity.Project;
import com.intecx.inscope.entity.RiskConfig;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.RiskConfigMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.RiskConfigRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskConfigService {

    private static final String PROJECT_NOT_FOUND = "Proyecto no encontrado";
    private static final String CONFIG_NOT_FOUND = "Configuración de riesgo no encontrada";
    private static final BigDecimal DEFAULT_MARGIN = BigDecimal.valueOf(15);

    private final RiskConfigRepository riskConfigRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public RiskConfigResponse getByProject(UUID projectId) {
        return RiskConfigMapper.toResponse(findByProject(projectId));
    }

    @Transactional
    public RiskConfigResponse update(UUID projectId, RiskLevelUpdateRequest request, UUID actorId) {
        Project project = projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND));

        RiskConfig config = riskConfigRepository.findByProjectId(projectId)
                .orElseGet(() -> RiskConfig.builder()
                        .project(project)
                        .contingencyMargin(DEFAULT_MARGIN)
                        .build());

        config.setLevel(request.level());
        if (request.contingencyMargin() != null) {
            config.setContingencyMargin(request.contingencyMargin());
        }
        config.setUpdatedAt(LocalDateTime.now());
        config.setUpdatedBy(actorId);

        return RiskConfigMapper.toResponse(riskConfigRepository.save(config));
    }

    private RiskConfig findByProject(UUID projectId) {
        return riskConfigRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIG_NOT_FOUND));
    }
}
