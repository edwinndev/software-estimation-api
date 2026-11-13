package com.intecx.estimation.mapper;

import com.intecx.estimation.dto.response.TechnicalTaskResponse;
import com.intecx.estimation.entity.TechnicalTask;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TechnicalTaskMapper {

    public TechnicalTaskResponse toResponse(TechnicalTask task) {
        return new TechnicalTaskResponse(
                task.getId(),
                task.getStoryId(),
                task.getTitle(),
                task.getDescription(),
                task.getEstimateHours(),
                task.getStatus(),
                task.getPosition(),
                task.getResponsibleProfileIds().stream().sorted().collect(Collectors.toList()),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
