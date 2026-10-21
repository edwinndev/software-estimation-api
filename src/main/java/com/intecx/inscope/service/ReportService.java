package com.intecx.inscope.service;

import com.intecx.inscope.dto.response.FileAttachmentResponse;
import java.util.UUID;

public interface ReportService {

    FileAttachmentResponse generateEstimationPdf(UUID projectId, UUID estimationId, UUID actorId);

    byte[] downloadEstimationPdf(UUID projectId, UUID estimationId);

    byte[] exportProjectsExcel();
}
