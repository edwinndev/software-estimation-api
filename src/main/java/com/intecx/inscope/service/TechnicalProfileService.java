package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import java.util.UUID;

public interface TechnicalProfileService {

    TechnicalProfileResponse create(CreateTechnicalProfileRequest request, UUID actorId);
}
