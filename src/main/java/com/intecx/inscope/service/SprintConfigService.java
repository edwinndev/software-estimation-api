package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.estimation.VelocityRequest;
import com.intecx.inscope.dto.response.SprintConfigResponse;
import java.util.UUID;

public interface SprintConfigService {

    SprintConfigResponse registerVelocity(UUID projectId, VelocityRequest request);
}