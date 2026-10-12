package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.estimation.DurationRequest;
import com.intecx.inscope.dto.request.estimation.VelocityRequest;
import com.intecx.inscope.dto.response.SprintConfigResponse;
import com.intecx.inscope.entity.SprintConfig;
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

    private final SprintConfigRepository sprintConfigRepository;
    private final SprintConfigMapper sprintConfigMapper;

    @Override
    @Transactional
    public SprintConfigResponse registerVelocity(UUID projectId, VelocityRequest request) {
        SprintConfig config = findOrCreate(projectId);
        config.setVelocity(request.velocity());
        return sprintConfigMapper.toResponse(sprintConfigRepository.save(config));
    }

    @Override
    @Transactional
    public SprintConfigResponse configureDuration(UUID projectId, DurationRequest request) {
        SprintConfig config = findOrCreate(projectId);
        config.setDuration(request.duration());
        config.setUnit(request.unit());
        return sprintConfigMapper.toResponse(sprintConfigRepository.save(config));
    }

    private SprintConfig findOrCreate(UUID projectId) {
        return sprintConfigRepository.findById(projectId)
                .orElseGet(() -> SprintConfig.withDefaults(projectId));
    }
}