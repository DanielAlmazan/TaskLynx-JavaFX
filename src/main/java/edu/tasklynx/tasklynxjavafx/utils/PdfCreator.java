package edu.tasklynx.tasklynxjavafx.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import edu.tasklynx.tasklynxjavafx.model.Payroll;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class PdfCreator {

    public static File createPayrollPDF(Payroll payroll, String dest) {

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("PAYROLL REPORT"));
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
            System.out.println("Error creating PDF: " + e.getMessage());
        }

        return null;
    }

    public static void createEmployeesWithoutTasksReport (List<Trabajador> employeesWithoutTasks) {
        try {
            PdfWriter writer = new PdfWriter("reports/employeesWithoutTasks.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            ImageData imageLogoData = ImageDataFactory.create("src/main/resources/img/png/TaskLynx-empty-logo.png");
            Image logo = new Image (imageLogoData);
            document.add(logo);

            ImageData imageTextData = ImageDataFactory.create("src/main/resources/img/png/txt-tasklynx.png");
            Image image = new Image (imageTextData);
            document.add(image);

            document.add(new Paragraph("EMPLOYEES WITHOUT TASKS:"));
            for (Trabajador trabajador : employeesWithoutTasks) {
                document.add(new Paragraph("- " + trabajador.getNombre() + " " + trabajador.getApellidos()));
            }

            document.close();
            System.out.println("PDF Created");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void generateGeneralReport(List<Trabajador> employees) {
        try {
            PdfWriter writer = new PdfWriter("reports/generalReport.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            ImageData imageLogoData = ImageDataFactory.create("src/main/resources/img/png/TaskLynx-empty-logo.png");
            Image logo = new Image (imageLogoData);
            document.add(logo);

            ImageData imageTextData = ImageDataFactory.create("src/main/resources/img/png/txt-tasklynx.png");
            Image image = new Image (imageTextData);
            document.add(image);

            document.add(new Paragraph("GENERAL REPORT:"));

            float[] columnWidths = {4, 4, 4, 4, 4};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            table.addCell(new Paragraph("Employee ID"));
            table.addCell(new Paragraph("Full name"));
            table.addCell(new Paragraph("DNI"));
            table.addCell(new Paragraph("Email"));
            table.addCell(new Paragraph("Speciality"));

            for (Trabajador trabajador : employees) {
                table.addCell(new Paragraph(String.valueOf(trabajador.getIdTrabajador())));
                table.addCell(new Paragraph(trabajador.getNombre() + " " + trabajador.getApellidos()));
                table.addCell(new Paragraph(trabajador.getDni()));
                table.addCell(new Paragraph(trabajador.getEmail()));
                table.addCell(new Paragraph(trabajador.getEspecialidad()));
            }

            document.add(table);
            document.close();
            System.out.println("PDF Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generatePaymentReport(String dest, LocalDate startDate, LocalDate endDate, List<Trabajo> tasks) {
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            ImageData imageLogoData = ImageDataFactory.create("src/main/resources/img/png/TaskLynx-empty-logo.png");
            Image logo = new Image (imageLogoData);
            document.add(logo);

            ImageData imageTextData = ImageDataFactory.create("src/main/resources/img/png/txt-tasklynx.png");
            Image image = new Image (imageTextData);
            document.add(image);

            document.add(new Paragraph("PAYMENT REPORT:"));
            document.add(new Paragraph("Date Range: " + startDate + " to " + endDate));

            float[] columnWidths = {2, 5, 4, 4, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            table.addCell(new Paragraph("Task ID"));
            table.addCell(new Paragraph("Employee ID"));
            table.addCell(new Paragraph("Starting Date"));
            table.addCell(new Paragraph("Ending Date"));
            table.addCell(new Paragraph("Time Spent"));
            table.addCell(new Paragraph("Salary"));

            for (Trabajo trabajo : tasks) {
                table.addCell(new Paragraph(String.valueOf(trabajo.getCodTrabajo())));
                table.addCell(new Paragraph(String.valueOf(trabajo.getIdTrabajador())));
                table.addCell(new Paragraph(trabajo.getFecIni().toString()));
                table.addCell(new Paragraph(trabajo.getFecFin().toString()));
                table.addCell(new Paragraph(String.valueOf(trabajo.getTiempo())));
                table.addCell(new Paragraph(String.valueOf(trabajo.getRemuneration())));
            }

            document.add(table);
            document.add(new Paragraph("Total payment: " + tasks.stream().map(Trabajo::getRemuneration).reduce(0.0, Double::sum) + " €"));
            document.close();
            System.out.println("PDF Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
