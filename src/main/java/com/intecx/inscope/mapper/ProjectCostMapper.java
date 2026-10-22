package com.intecx.inscope.mapper;

import com.intecx.inscope.dto.response.CostAmountResponse;
import com.intecx.inscope.dto.response.ProfileCostResponse;
import com.intecx.inscope.dto.response.ProjectCostResponse;
import com.intecx.inscope.dto.response.TaskCostResponse;
import com.intecx.inscope.dto.response.TaskProfileCostResponse;
import com.intecx.inscope.enumeration.CurrencyCode;
import com.intecx.inscope.repository.ProjectCostProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProjectCostMapper {

    public TaskProfileCostResponse toContributionResponse(ProjectCostProjection row, BigDecimal hours, BigDecimal cost) {
        return TaskProfileCostResponse.builder()
                .technicalProfileId(row.getTechnicalProfileId())
                .hours(hours)
                .hourlyRate(row.getHourlyRate())
                .cost(new CostAmountResponse(cost, CurrencyCode.valueOf(row.getCurrency())))
                .build();
    }

    public TaskCostResponse toTaskResponse(UUID taskId, CostAmountResponse total,
            List<TaskProfileCostResponse> contributions) {
        return TaskCostResponse.builder()
                .taskId(taskId)
                .total(total)
                .contributions(contributions)
                .build();
    }

    public ProjectCostResponse toResponse(UUID projectId, CostAmountResponse total,
            List<TaskCostResponse> tasks, List<ProfileCostResponse> profiles) {
        return ProjectCostResponse.builder()
                .projectId(projectId)
                .total(total)
                .tasks(tasks)
                .profiles(profiles)
                .build();
    }
}
