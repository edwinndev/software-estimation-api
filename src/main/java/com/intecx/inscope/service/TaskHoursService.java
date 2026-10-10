package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.TaskHoursResponse;
import java.util.UUID;

public interface TaskHoursService {

    TaskHoursResponse getTaskHours(UUID projectId);
}