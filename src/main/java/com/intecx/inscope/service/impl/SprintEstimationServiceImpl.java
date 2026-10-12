package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.response.SprintEstimationResponse;
import com.intecx.inscope.entity.SprintConfig;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.SprintEstimationMapper;
import com.intecx.inscope.repository.ProjectRepository;
import com.intecx.inscope.repository.SprintConfigRepository;
import com.intecx.inscope.repository.UserStoryRepository;
import com.intecx.inscope.service.SprintEstimationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SprintEstimationServiceImpl implements SprintEstimationService {

    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final SprintConfigRepository sprintConfigRepository;
    private final SprintEstimationMapper sprintEstimationMapper;

    @Override
    @Transactional(readOnly = true)
    public SprintEstimationResponse estimateSprints(UUID projectId) {
        projectRepository.findActiveById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        long totalStoryPoints = userStoryRepository.sumActiveStoryPointsByProjectId(projectId);
        int velocity = sprintConfigRepository.findById(projectId)
                .orElseGet(() -> SprintConfig.withDefaults(projectId))
                .getVelocity();

        return sprintEstimationMapper.toResponse(totalStoryPoints, velocity, Math.ceilDiv(totalStoryPoints, velocity));
    }
}