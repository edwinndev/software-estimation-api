package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.ProjectEstimationResponse;
import com.intecx.inscope.dto.response.ReportSnapshotDetailResponse;
import com.intecx.inscope.entity.ReportSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportSnapshotMapper implements BaseMapper<ReportSnapshot, ProjectEstimationResponse> {

    private final FileAttachmentMapper fileAttachmentMapper;

    @Override
    public ProjectEstimationResponse toResponse(ReportSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        return ProjectEstimationResponse.builder()
                .id(snapshot.getId())
                .baseEffortPoints(snapshot.getBaseEffortPoints())
                .totalEffortHours(snapshot.getTotalEffortHours())
                .totalTime(snapshot.getTotalTime())
                .timeUnit(snapshot.getTimeUnit())
                .totalCost(snapshot.getTotalCost())
                .createdAt(snapshot.getCreatedAt())
                .build();
    }

    public ReportSnapshotDetailResponse toDetailResponse(ReportSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        return ReportSnapshotDetailResponse.builder()
                .id(snapshot.getId())
                .projectId(snapshot.getProject().getId())
                .projectName(snapshot.getProjectName())
                .projectStatus(snapshot.getProjectStatus())
                .projectType(snapshot.getProjectType())
                .projectOwner(snapshot.getProjectOwner())
                .baseEffortPoints(snapshot.getBaseEffortPoints())
                .totalSprints(snapshot.getTotalSprints())
                .totalEffortHours(snapshot.getTotalEffortHours())
                .storiesTotal(snapshot.getStoriesTotal())
                .storiesWithPoints(snapshot.getStoriesWithPoints())
                .tasksTotal(snapshot.getTasksTotal())
                .tasksWithHours(snapshot.getTasksWithHours())
                .baseTime(snapshot.getBaseTime())
                .timeUnit(snapshot.getTimeUnit())
                .baseCost(snapshot.getBaseCost())
                .riskLevel(snapshot.getRiskLevel())
                .contingencyMarginPercentage(snapshot.getContingencyMarginPercentage())
                .contingencyTime(snapshot.getContingencyTime())
                .contingencyCost(snapshot.getContingencyCost())
                .totalTime(snapshot.getTotalTime())
                .totalCost(snapshot.getTotalCost())
                .createdBy(snapshot.getCreatedBy())
                .createdAt(snapshot.getCreatedAt())
                .file(fileAttachmentMapper.toResponse(snapshot.getFile()))
                .build();
    }
}
