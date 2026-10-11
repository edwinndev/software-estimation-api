package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.estimation.StoryPointsRequest;
import com.intecx.inscope.dto.response.UserStoryResponse;
import java.util.UUID;

public interface UserStoryService {

    UserStoryResponse assignStoryPoints(UUID id, StoryPointsRequest request);
}