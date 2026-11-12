package com.intecx.estimation.service;

import com.intecx.estimation.common.PaginatedResponse;
import com.intecx.estimation.common.QueryFields;
import com.intecx.estimation.common.QueryRequest;
import com.intecx.estimation.common.QuerySupport;
import com.intecx.estimation.dto.request.TaskResponsiblesRequest;
import com.intecx.estimation.dto.request.TechnicalTaskRequest;
import com.intecx.estimation.dto.request.TechnicalTaskUpdateRequest;
import com.intecx.estimation.dto.response.TechnicalTaskResponse;
import com.intecx.estimation.entity.TechnicalTask;
import com.intecx.estimation.exception.ResourceNotFoundException;
import com.intecx.estimation.mapper.TechnicalTaskMapper;
import com.intecx.estimation.repository.TechnicalTaskRepository;
import com.intecx.estimation.repository.UserStoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TechnicalTaskService {

    private static final QueryFields FIELDS = QueryFields.sortingBy("position")
            .filter("storyId", "storyId")
            .filter("status", "status")
            .filter("search", "title", "description")
            .sortable("position", "title", "status", "estimateHours", "createdAt", "updatedAt");

    private final TechnicalTaskRepository technicalTaskRepository;
    private final UserStoryRepository userStoryRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<TechnicalTaskResponse> search(QueryRequest request) {
        return QuerySupport.search(
                technicalTaskRepository,
                FIELDS,
                request,
                TechnicalTaskMapper::toResponse,
                "taskResponse"
        );
    }

    @Transactional
    public TechnicalTaskResponse create(TechnicalTaskRequest request) {
        if (!userStoryRepository.existsById(request.storyId())) {
            throw new ResourceNotFoundException("Historia asociada no encontrada");
        }
        List<TechnicalTask> tasks = technicalTaskRepository.findAllByStoryIdOrderByPositionAsc(request.storyId());
        TechnicalTask task = new TechnicalTask();
        task.setStoryId(request.storyId());
        task.setTitle(request.title().strip());
        task.setDescription(request.description().strip());
        task.setEstimateHours(request.estimateHours());
        task.setStatus(request.status());
        task.setPosition(nextPosition(tasks));
        return TechnicalTaskMapper.toResponse(technicalTaskRepository.save(task));
    }

    @Transactional
    public TechnicalTaskResponse update(UUID taskId, TechnicalTaskUpdateRequest request) {
        TechnicalTask task = findTask(taskId);
        task.setTitle(request.title().strip());
        task.setDescription(request.description().strip());
        task.setEstimateHours(request.estimateHours());
        task.setStatus(request.status());
        if (request.position() != null) {
            reorder(task, request.position());
        }
        return TechnicalTaskMapper.toResponse(technicalTaskRepository.save(task));
    }

    @Transactional
    public TechnicalTaskResponse updateResponsibles(UUID taskId, TaskResponsiblesRequest request) {
        TechnicalTask task = findTask(taskId);
        task.getResponsibleProfileIds().clear();
        task.getResponsibleProfileIds().addAll(request.profileIds());
        return TechnicalTaskMapper.toResponse(technicalTaskRepository.save(task));
    }

    @Transactional
    public void delete(UUID taskId) {
        technicalTaskRepository.delete(findTask(taskId));
    }

    private TechnicalTask findTask(UUID taskId) {
        return technicalTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea técnica no encontrada"));
    }

    private void reorder(TechnicalTask task, int requestedPosition) {
        List<TechnicalTask> tasks = new ArrayList<>(
                technicalTaskRepository.findAllByStoryIdOrderByPositionAsc(task.getStoryId()));
        tasks.removeIf(item -> item.getId().equals(task.getId()));
        tasks.add(Math.max(0, Math.min(requestedPosition, tasks.size())), task);
        for (int position = 0; position < tasks.size(); position++) {
            tasks.get(position).setPosition(position);
        }
        technicalTaskRepository.saveAll(tasks);
    }

    private int nextPosition(List<TechnicalTask> tasks) {
        return tasks.stream().mapToInt(TechnicalTask::getPosition).max().orElse(-1) + 1;
    }
}
