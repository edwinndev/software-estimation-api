package com.intecx.inscope.service;

import com.intecx.inscope.dto.request.estimation.HourAdjustmentRequest;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import java.util.UUID;

public interface TaskHourAdjustmentService {

    TaskProfileHoursResponse adjustHours(UUID taskId, UUID profileId, HourAdjustmentRequest request);
}