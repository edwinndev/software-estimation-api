package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.risk.UpdateRiskConfigRequest;
import com.intecx.inscope.dto.response.RiskConfigResponse;
import java.util.UUID;

public interface RiskConfigService {

    RiskConfigResponse getByProject(UUID projectId);

    RiskConfigResponse update(UUID projectId, UpdateRiskConfigRequest request, UUID actorId);
}
