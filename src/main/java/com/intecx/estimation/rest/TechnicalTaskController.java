package com.intecx.estimation.rest;

import com.intecx.estimation.common.PaginatedResponse;
import com.intecx.estimation.common.QueryRequest;
import com.intecx.estimation.dto.request.TaskResponsiblesRequest;
import com.intecx.estimation.dto.request.TechnicalTaskRequest;
import com.intecx.estimation.dto.request.TechnicalTaskUpdateRequest;
import com.intecx.estimation.dto.response.TechnicalTaskResponse;
import com.intecx.estimation.service.TechnicalTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Technical tasks", description = "Gestión de tareas técnicas del backlog")
public class TechnicalTaskController {

    private final TechnicalTaskService technicalTaskService;

    @PostMapping("/search")
    @Operation(summary = "Buscar tareas técnicas")
    public PaginatedResponse<TechnicalTaskResponse> search(@Valid @RequestBody QueryRequest request) {
        return technicalTaskService.search(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una tarea técnica")
    public TechnicalTaskResponse create(@Valid @RequestBody TechnicalTaskRequest request) {
        return technicalTaskService.create(request);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Actualizar una tarea técnica")
    public TechnicalTaskResponse update(
            @PathVariable UUID taskId,
            @Valid @RequestBody TechnicalTaskUpdateRequest request
    ) {
        return technicalTaskService.update(taskId, request);
    }

    @PutMapping("/{taskId}/responsibles")
    @Operation(summary = "Asignar responsables a una tarea técnica")
    public TechnicalTaskResponse updateResponsibles(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskResponsiblesRequest request
    ) {
        return technicalTaskService.updateResponsibles(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una tarea técnica")
    public void delete(@PathVariable UUID taskId) {
        technicalTaskService.delete(taskId);
    }
}
