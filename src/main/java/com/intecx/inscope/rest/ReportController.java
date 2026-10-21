package com.intecx.inscope.rest;

import com.intecx.inscope.dto.response.FileAttachmentResponse;
import com.intecx.inscope.security.UserPrincipal;
import com.intecx.inscope.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reportes", description = "Generación y descarga de reportes PDF/Excel")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Generar PDF de una estimación y guardarlo en BunnyCDN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "PDF generado y almacenado"),
            @ApiResponse(responseCode = "400", description = "BunnyCDN no configurado o error de almacenamiento"),
            @ApiResponse(responseCode = "404", description = "Estimación no encontrada")
    })
    @PostMapping("/projects/{projectId}/estimations/{estimationId}/pdf")
    @PreAuthorize("hasAuthority('report:read')")
    public ResponseEntity<FileAttachmentResponse> generatePdf(
            @PathVariable UUID projectId,
            @PathVariable UUID estimationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileAttachmentResponse response = reportService.generateEstimationPdf(
                projectId, estimationId, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Descargar el PDF de una estimación desde BunnyCDN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF descargado"),
            @ApiResponse(responseCode = "404", description = "Estimación o PDF no encontrado")
    })
    @GetMapping("/projects/{projectId}/estimations/{estimationId}/pdf")
    @PreAuthorize("hasAuthority('report:read')")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable UUID projectId,
            @PathVariable UUID estimationId
    ) {
        byte[] pdf = reportService.downloadEstimationPdf(projectId, estimationId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"estimacion-%s.pdf\"".formatted(estimationId))
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Exportar proyectos y su última estimación a Excel (dashboard)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Excel generado")
    })
    @GetMapping("/projects/excel")
    @PreAuthorize("hasAuthority('report:read')")
    public ResponseEntity<byte[]> exportProjectsExcel() {
        byte[] excel = reportService.exportProjectsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proyectos.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
