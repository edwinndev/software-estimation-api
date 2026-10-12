package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.SprintEstimationResponse;
import java.util.UUID;

public interface SprintEstimationService {

    SprintEstimationResponse estimateSprints(UUID projectId);
}