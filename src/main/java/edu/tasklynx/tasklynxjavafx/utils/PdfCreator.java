package edu.tasklynx.tasklynxjavafx.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import edu.tasklynx.tasklynxjavafx.model.Payroll;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;

import java.io.File;

public class PdfCreator {

    public static File createPayrollPDF(Payroll payroll, String dest) {

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Payroll Report"));
            document.add(new Paragraph("Employee: " + payroll.getTrabajador().getNombre() + " " + payroll.getTrabajador().getApellidos()));
            document.add(new Paragraph("DNI: " + payroll.getTrabajador().getDni()));
            document.add(new Paragraph("Email: " + payroll.getTrabajador().getEmail()));
            document.add(new Paragraph("Speciality: " + payroll.getTrabajador().getEspecialidad()));
            document.add(new Paragraph("Date Range: " + payroll.getFecIni() + " to " + payroll.getFecFin()));
            document.add(new Paragraph("Total Time Spent: " + payroll.getTiempo() + " hours"));
            document.add(new Paragraph("Total Salary: " + payroll.getSalario() + " €"));

            float[] columnWidths = {2, 4, 4, 4, 4};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            table.addCell(new Paragraph("Task ID"));
            table.addCell(new Paragraph("Starting Date"));
            table.addCell(new Paragraph("Ending Date"));
            table.addCell(new Paragraph("Time Spent"));
            table.addCell(new Paragraph("Salary"));

            for (Trabajo trabajo : payroll.getTrabajos()) {
                table.addCell(new Paragraph(String.valueOf(trabajo.getCodTrabajo())));
                table.addCell(new Paragraph(trabajo.getFecIni().toString()));
                table.addCell(new Paragraph(trabajo.getFecFin().toString()));
                table.addCell(new Paragraph(String.valueOf(trabajo.getTiempo())));
                table.addCell(new Paragraph(String.valueOf(trabajo.getRemuneration())));
            }

            document.add(table);
            document.close();
            System.out.println("PDF Created");
            return new File(dest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
