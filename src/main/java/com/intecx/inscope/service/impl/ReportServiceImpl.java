package com.intecx.inscope.service.impl;

import com.intecx.inscope.common.PaginatedResponse;
import com.intecx.inscope.common.PaginationConstants;
import com.intecx.inscope.common.PaginationRequest;
import com.intecx.inscope.common.QueryRequest;
import com.intecx.inscope.dto.response.FileAttachmentResponse;
import com.intecx.inscope.dto.response.ProjectResponse;
import com.intecx.inscope.entity.FileAttachment;
import com.intecx.inscope.entity.ReportSnapshot;
import com.intecx.inscope.enumeration.AttachmentType;
import com.intecx.inscope.exception.ResourceNotFoundException;
import com.intecx.inscope.mapper.FileAttachmentMapper;
import com.intecx.inscope.repository.FileAttachmentRepository;
import com.intecx.inscope.repository.ReportSnapshotRepository;
import com.intecx.inscope.service.BunnyStorageService;
import com.intecx.inscope.service.ProjectService;
import com.intecx.inscope.service.ReportService;
import com.intecx.inscope.service.support.BunnyPaths;
import com.intecx.inscope.service.support.EstimationPdfGenerator;
import com.intecx.inscope.service.support.ProjectsExcelExporter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final String ESTIMATION_NOT_FOUND = "Estimación no encontrada";
    private static final String PDF_NOT_FOUND = "PDF de la estimación no encontrado";
    private static final String PDF_CONTENT_TYPE = MediaType.APPLICATION_PDF_VALUE;

    private final ReportSnapshotRepository reportSnapshotRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final BunnyStorageService bunnyStorageService;
    private final ProjectService projectService;
    private final FileAttachmentMapper fileAttachmentMapper;

    @Override
    @Transactional
    public FileAttachmentResponse generateEstimationPdf(UUID projectId, UUID estimationId, UUID actorId) {
        ReportSnapshot snapshot = requireSnapshot(projectId, estimationId);
        byte[] pdf = EstimationPdfGenerator.generate(snapshot);
        String fileName = estimationId + ".pdf";
        String storagePath = BunnyPaths.reportPdf(estimationId);

        bunnyStorageService.upload(storagePath, pdf, PDF_CONTENT_TYPE);

        FileAttachment previous = snapshot.getFile();
        if (previous != null) {
            snapshot.setFile(null);
            reportSnapshotRepository.saveAndFlush(snapshot);
            bunnyStorageService.delete(previous.getStoragePath());
            fileAttachmentRepository.delete(previous);
            fileAttachmentRepository.flush();
        }

        FileAttachment saved = fileAttachmentRepository.save(FileAttachment.builder()
                .type(AttachmentType.PDF)
                .fileName(fileName)
                .contentType(PDF_CONTENT_TYPE)
                .sizeBytes((long) pdf.length)
                .storagePath(storagePath)
                .createdAt(LocalDateTime.now())
                .createdBy(actorId)
                .build());

        snapshot.setFile(saved);
        reportSnapshotRepository.save(snapshot);
        return fileAttachmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadEstimationPdf(UUID projectId, UUID estimationId) {
        ReportSnapshot snapshot = requireSnapshot(projectId, estimationId);
        if (snapshot.getFile() == null) {
            throw new ResourceNotFoundException(PDF_NOT_FOUND);
        }
        return bunnyStorageService.download(snapshot.getFile().getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportProjectsExcel() {
        List<ProjectResponse> projects = new ArrayList<>();
        int pageNumber = PaginationConstants.FIRST_PAGE;
        boolean hasNext;
        do {
            PaginatedResponse<ProjectResponse> page = projectService.search(new QueryRequest(
                    null,
                    new PaginationRequest("", PaginationConstants.MAX_PAGE_SIZE, pageNumber, null)
            ));
            projects.addAll(page.getItems());
            hasNext = page.getMeta().hasNext();
            pageNumber++;
        } while (hasNext);
        return ProjectsExcelExporter.export(projects);
    }

    private ReportSnapshot requireSnapshot(UUID projectId, UUID estimationId) {
        return reportSnapshotRepository
                .findByIdAndProject_IdAndProject_DeletedAtIsNull(estimationId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(ESTIMATION_NOT_FOUND));
    }
}
