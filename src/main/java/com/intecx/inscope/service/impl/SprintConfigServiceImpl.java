package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.estimation.VelocityRequest;
import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.entity.SprintConfig;
import com.intecx.inscope.enumeration.SprintUnit;
import com.intecx.inscope.mapper.SprintConfigMapper;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.service.SprintConfigService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SprintConfigServiceImpl implements SprintConfigService {

    // Mismos valores por defecto que sprint_configs en V1__schema.sql.
    private static final int DEFAULT_DURATION = 2;
    private static final SprintUnit DEFAULT_UNIT = SprintUnit.DAYS;

    private final SprintConfigRepository sprintConfigRepository;
    private final SprintConfigMapper sprintConfigMapper;

    @Override
    @Transactional
    public SprintConfigResponse registerVelocity(UUID projectId, VelocityRequest request) {
        SprintConfig config = sprintConfigRepository.findById(projectId)
                .orElseGet(() -> newConfig(projectId));
        config.setVelocity(request.velocity());
        return sprintConfigMapper.toResponse(sprintConfigRepository.save(config));
    }

    private SprintConfig newConfig(UUID projectId) {
        SprintConfig config = new SprintConfig();
        config.setProjectId(projectId);
        config.setDuration(DEFAULT_DURATION);
        config.setUnit(DEFAULT_UNIT);
        return config;
    }
}