package edu.tasklynx.tasklynxjavafx.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
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

            // Task Lynx logo
            ImageData imageLogoData = ImageDataFactory.create("src/main/resources/img/png/TaskLynx-empty-logo.png");
            Image logo = new Image (imageLogoData);
            document.add(logo);

            // Task lynx text
            ImageData imageTextData = ImageDataFactory.create("src/main/resources/img/png/txt-tasklynx.png");
            Image image = new Image (imageTextData);
            document.add(image);

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Title
            Text title = new Text("PAYROLL REPORT").setBold().setFontSize(16);
            document.add(new Paragraph(title));

            // Line separator
            SolidLine solidLine = new SolidLine();
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(lineSeparator);

            // Employee information
            document.add(new Paragraph().add(new Text("Employee: ").setBold()).add(new Text(payroll.getTrabajador().getNombre() + " " + payroll.getTrabajador().getApellidos())));
            document.add(new Paragraph().add(new Text("DNI: ").setBold()).add(new Text(payroll.getTrabajador().getDni())));
            document.add(new Paragraph().add(new Text("Email: ").setBold()).add(new Text(payroll.getTrabajador().getEmail())));
            document.add(new Paragraph().add(new Text("Speciality: ").setBold()).add(new Text(payroll.getTrabajador().getEspecialidad())));
            document.add(new Paragraph().add(new Text("Date Range: ").setBold()).add(new Text(payroll.getFecIni() + " to " + payroll.getFecFin())));
            document.add(new Paragraph().add(new Text("Total Time Spent: ").setBold()).add(new Text(payroll.getTiempo() + " hours")));
            document.add(new Paragraph().add(new Text("Total Salary: ").setBold()).add(new Text(payroll.getSalario() + " €")));

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Tasks title
            Text tasksTitle = new Text("TASKS:").setBold().setFontSize(14);
            document.add(new Paragraph(tasksTitle));

            // Line separator
            document.add(lineSeparator);
            document.add(new Paragraph(" "));

            // Table
            float[] columnWidths = {3, 4, 4, 4, 4};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // First row
            table.addCell(new Cell().add(new Paragraph("Task ID").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Starting Date").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Ending Date").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Time Spent").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Salary").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);

            // Tasks rows
            for (Trabajo trabajo : payroll.getTrabajos()) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getCodTrabajo())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajo.getFecIni().toString()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajo.getFecFin().toString()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getTiempo())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getRemuneration())))).setTextAlignment(TextAlignment.CENTER);
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

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Title
            Text title = new Text("EMPLOYEES WITHOUT TASKS:").setBold().setFontSize(16);
            document.add(new Paragraph(title));

            // Line separator
            SolidLine solidLine = new SolidLine();
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(lineSeparator);

            // List of employees without tasks
            for (Trabajador trabajador : employeesWithoutTasks) {
                document.add(new Paragraph("· " + trabajador.getNombre() + " " + trabajador.getApellidos()));
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

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Title
            Text title = new Text("GENERAL REPORT").setBold().setFontSize(16);
            document.add(new Paragraph(title));

            // Line separator
            SolidLine solidLine = new SolidLine();
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(lineSeparator);

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Table
            float[] columnWidths = {4, 4, 4, 4, 4};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // First row
            table.addCell(new Cell().add(new Paragraph("Employee ID").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Full name").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("DNI").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Email").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Speciality").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);

            // Employees rows
            for (Trabajador trabajador : employees) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajador.getIdTrabajador())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajador.getNombre() + " " + trabajador.getApellidos()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajador.getDni()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajador.getEmail()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajador.getEspecialidad()))).setTextAlignment(TextAlignment.CENTER);
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

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Title
            Text title = new Text("PAYMENT REPORT").setBold().setFontSize(16);
            document.add(new Paragraph(title));

            Text dateRange = new Text("Date Range: " + startDate + " to " + endDate).setItalic().setFontSize(12);
            document.add(new Paragraph(dateRange));

            // Line separator
            SolidLine solidLine = new SolidLine();
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(lineSeparator);

            // Empty lines
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


            // Table
            float[] columnWidths = {2, 5, 3, 3, 3, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));

            // First row
            table.addCell(new Cell().add(new Paragraph("Task ID").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Employee").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Starting Date").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Ending Date").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Time Spent").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);
            table.addCell(new Cell().add(new Paragraph("Salary").setBold()).setBackgroundColor(new DeviceRgb(207, 207, 207))).setTextAlignment(TextAlignment.CENTER);

            for (Trabajo trabajo : tasks) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getCodTrabajo())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getIdTrabajador())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajo.getFecIni().toString()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(trabajo.getFecFin().toString()))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getTiempo())))).setTextAlignment(TextAlignment.CENTER);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(trabajo.getRemuneration())))).setTextAlignment(TextAlignment.CENTER);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Text totalPayment = new Text("Total payment: " + tasks.stream().map(Trabajo::getRemuneration).reduce(0.0, Double::sum) + " €").setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT);
            document.add(new Paragraph(totalPayment).setTextAlignment(TextAlignment.RIGHT));

            document.close();
            System.out.println("PDF Created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
