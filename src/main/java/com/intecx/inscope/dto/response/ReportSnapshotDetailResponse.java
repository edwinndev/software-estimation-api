package com.intecx.inscope.dto.response;

import com.intecx.inscope.enumeration.ProjectStatus;
import com.intecx.inscope.enumeration.ProjectType;
import com.intecx.inscope.enumeration.RiskLevel;
import com.intecx.inscope.enumeration.SprintUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReportSnapshotDetailResponse(
        UUID id,
        UUID projectId,
        String projectName,
        ProjectStatus projectStatus,
        ProjectType projectType,
        String projectOwner,
        Integer baseEffortPoints,
        Integer totalSprints,
        BigDecimal totalEffortHours,
        Integer storiesTotal,
        Integer storiesWithPoints,
        Integer tasksTotal,
        Integer tasksWithHours,
        BigDecimal baseTime,
        SprintUnit timeUnit,
        BigDecimal baseCost,
        RiskLevel riskLevel,
        BigDecimal contingencyMarginPercentage,
        BigDecimal contingencyTime,
        BigDecimal contingencyCost,
        BigDecimal totalTime,
        BigDecimal totalCost,
        UUID createdBy,
        LocalDateTime createdAt,
        FileAttachmentResponse file
) {
}
