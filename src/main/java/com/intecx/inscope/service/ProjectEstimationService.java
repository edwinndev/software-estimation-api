package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.project.ProjectContingencyRequest;
import com.intecx.inscope.dto.response.ProjectContingencyResponse;
import com.intecx.inscope.dto.response.ProjectEstimationResponse;
import com.intecx.inscope.dto.response.ReportSnapshotDetailResponse;
import java.util.List;
import java.util.UUID;

public interface ProjectEstimationService {

    ReportSnapshotDetailResponse consolidate(UUID projectId);

    ReportSnapshotDetailResponse create(UUID projectId, UUID actorId);

    List<ProjectEstimationResponse> listByProject(UUID projectId);

    ReportSnapshotDetailResponse getById(UUID projectId, UUID estimationId);

    ProjectContingencyResponse calculate(UUID projectId, ProjectContingencyRequest request);
}
