package com.intecx.inscope.service.support;

import com.intecx.inscope.dto.response.ProjectEstimationResponse;
import com.intecx.inscope.dto.response.ProjectResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@UtilityClass
public class ProjectsExcelExporter {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] export(List<ProjectResponse> projects) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Proyectos");
            Row header = sheet.createRow(0);
            String[] columns = {
                    "ID", "Nombre", "Tipo", "Estado", "Inicio", "Fin planificado",
                    "Última estimación", "Puntos", "Horas", "Tiempo total", "Unidad", "Costo total"
            };
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (ProjectResponse project : projects) {
                ProjectEstimationResponse latest = project.estimations() == null || project.estimations().isEmpty()
                        ? null
                        : project.estimations().getFirst();
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(project.id() == null ? "" : project.id().toString());
                row.createCell(1).setCellValue(nullToEmpty(project.name()));
                row.createCell(2).setCellValue(project.type() == null ? "" : project.type().name());
                row.createCell(3).setCellValue(project.status() == null ? "" : project.status().name());
                row.createCell(4).setCellValue(project.startDate() == null ? "" : project.startDate().format(DATE));
                row.createCell(5).setCellValue(project.plannedDate() == null ? "" : project.plannedDate().format(DATE));
                row.createCell(6).setCellValue(latest == null || latest.createdAt() == null
                        ? ""
                        : latest.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                row.createCell(7).setCellValue(latest == null || latest.baseEffortPoints() == null
                        ? 0
                        : latest.baseEffortPoints());
                row.createCell(8).setCellValue(latest == null || latest.totalEffortHours() == null
                        ? 0
                        : latest.totalEffortHours().doubleValue());
                row.createCell(9).setCellValue(latest == null || latest.totalTime() == null
                        ? 0
                        : latest.totalTime().doubleValue());
                row.createCell(10).setCellValue(latest == null || latest.timeUnit() == null
                        ? ""
                        : latest.timeUnit().name());
                row.createCell(11).setCellValue(latest == null || latest.totalCost() == null
                        ? 0
                        : latest.totalCost().doubleValue());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el Excel de proyectos", ex);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
