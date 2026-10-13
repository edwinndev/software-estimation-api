package com.intecx.inscope.service.impl;

import com.intecx.inscope.dto.request.estimation.HourAdjustmentRequest;
import com.intecx.inscope.dto.response.TaskProfileHoursResponse;
import com.intecx.inscope.entity.Task;
import com.intecx.inscope.entity.TaskHourAdjustment;
import com.intecx.inscope.entity.TaskProfileId;
import com.intecx.inscope.entity.TechnicalProfile;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.TaskHoursMapper;
import com.intecx.inscope.repository.TaskAssignmentRepository;
import com.intecx.inscope.repository.TaskHourAdjustmentRepository;
import com.intecx.inscope.repository.TaskRepository;
import com.intecx.inscope.repository.TechnicalProfileRepository;
import com.intecx.inscope.service.TaskHourAdjustmentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskHourAdjustmentServiceImpl implements TaskHourAdjustmentService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskHourAdjustmentRepository taskHourAdjustmentRepository;
    private final TechnicalProfileRepository technicalProfileRepository;
    private final TaskHoursMapper taskHoursMapper;

    @Override
    @Transactional
    public TaskProfileHoursResponse adjustHours(UUID taskId, UUID profileId, HourAdjustmentRequest request) {
        Task task = taskRepository.findActiveById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
        TaskProfileId id = new TaskProfileId(taskId, profileId);
        if (!taskAssignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("El perfil técnico no está asignado a la tarea");
        }
        TechnicalProfile profile = technicalProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil técnico no encontrado"));

        TaskHourAdjustment adjustment = taskHourAdjustmentRepository.findById(id)
                .orElseGet(() -> newAdjustment(id));
        adjustment.setHours(request.hours());
        adjustment.setReason(request.reason().trim());
        taskHourAdjustmentRepository.save(adjustment);

        return taskHoursMapper.toRowResponse(
                task, profileId, profile.getName(), adjustment.getHours(), adjustment.getReason());
    }

    private TaskHourAdjustment newAdjustment(TaskProfileId id) {
        TaskHourAdjustment adjustment = new TaskHourAdjustment();
        adjustment.setId(id);
        return adjustment;
    }
}