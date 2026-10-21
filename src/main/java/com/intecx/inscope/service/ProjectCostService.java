package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.ProjectCostResponse;
import java.util.UUID;

public interface ProjectCostService {

    ProjectCostResponse getCosts(UUID projectId);
}
