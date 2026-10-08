package com.intecx.inscope.service;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.request.profile.CreateTechnicalProfileRequest;
import com.intecx.inscope.dto.request.profile.UpdateCerRequest;
import com.intecx.inscope.dto.request.profile.UpdateTechnicalProfileRequest;
import com.intecx.inscope.dto.response.TechnicalProfileResponse;
import java.util.UUID;

public interface TechnicalProfileService {

    PaginatedResponse<TechnicalProfileResponse> search(QueryRequest query);

    TechnicalProfileResponse getById(UUID id);

    TechnicalProfileResponse create(CreateTechnicalProfileRequest request, UUID actorId);

    TechnicalProfileResponse update(UUID id, UpdateTechnicalProfileRequest request, UUID actorId);

    TechnicalProfileResponse updateCer(UUID id, UpdateCerRequest request, UUID actorId);
}
