package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.TimeEstimationResponse;
import java.util.UUID;

public interface TimeEstimationService {

    TimeEstimationResponse estimateTime(UUID projectId);
}