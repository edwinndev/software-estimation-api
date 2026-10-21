package com.intecx.inscope.service.support;

import com.intecx.inscope.entity.ReportSnapshot;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EstimationPdfGenerator {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generate(ReportSnapshot snapshot) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Reporte de estimación - Intecx InScope", title));
            document.add(new Paragraph(" "));
            document.add(line(body, "Proyecto", snapshot.getProjectName()));
            document.add(line(body, "Tipo", snapshot.getProjectType()));
            document.add(line(body, "Estado", snapshot.getProjectStatus().name()));
            document.add(line(body, "Responsable", snapshot.getProjectOwner()));
            document.add(line(body, "Fecha", snapshot.getCreatedAt().format(DATE_TIME)));
            document.add(new Paragraph(" "));
            document.add(line(body, "Puntos de esfuerzo", String.valueOf(snapshot.getBaseEffortPoints())));
            document.add(line(body, "Sprints", String.valueOf(snapshot.getTotalSprints())));
            document.add(line(body, "Horas de esfuerzo", snapshot.getTotalEffortHours().toPlainString()));
            document.add(line(body, "Historias", snapshot.getStoriesTotal() + " (" + snapshot.getStoriesWithPoints() + " con puntos)"));
            document.add(line(body, "Tareas", snapshot.getTasksTotal() + " (" + snapshot.getTasksWithHours() + " con horas)"));
            document.add(new Paragraph(" "));
            document.add(line(body, "Tiempo base", snapshot.getBaseTime() + " " + snapshot.getTimeUnit()));
            document.add(line(body, "Costo base", snapshot.getBaseCost().toPlainString()));
            document.add(line(body, "Nivel de riesgo", snapshot.getRiskLevel().name()));
            document.add(line(body, "Margen de contingencia", snapshot.getContingencyMarginPercentage() + "%"));
            document.add(line(body, "Tiempo contingencia", snapshot.getContingencyTime().toPlainString()));
            document.add(line(body, "Costo contingencia", snapshot.getContingencyCost().toPlainString()));
            document.add(line(body, "Tiempo total", snapshot.getTotalTime() + " " + snapshot.getTimeUnit()));
            document.add(line(body, "Costo total", snapshot.getTotalCost().toPlainString()));

            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException ex) {
            throw new IllegalStateException("No se pudo generar el PDF de estimación", ex);
        }
    }

    private Paragraph line(Font font, String label, String value) {
        return new Paragraph(label + ": " + value, font);
    }
}
