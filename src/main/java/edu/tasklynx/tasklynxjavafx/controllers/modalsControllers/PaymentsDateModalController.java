package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Payroll;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.PdfCreator;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PaymentsDateModalController {

    @FXML
    public Button btnGeneratePaymentReport;
    @FXML
    public Button btnCancel;
    @FXML
    public DatePicker dpStartingDate;
    @FXML
    public DatePicker dpEndingDate;

    private List<Trabajo> tasksByDateRange;

    private Gson gson;

    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public CompletableFuture<List<Trabajo>> getTasksByDateRange(LocalDate startingDate, LocalDate endingDate) {
        String url = ServiceUtils.SERVER + "/trabajos/completados";
        url += "?fechaIni=" + startingDate + "&fechaFin=" + endingDate;

        // /trabajos/completados?fechaIni=2019-01-01&fechaFin=2025-03-02

        return ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenApply(response -> response.getJobs())
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return List.of();
                });
    }

    public void onGeneratePaymentReportBtn() {
        LocalDate startingDate = dpStartingDate.getValue();
        LocalDate endingDate = dpEndingDate.getValue();

        if (startingDate == null || endingDate == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Invalid date range", "Invalid date range", "Please select a valid date range.").showAndWait();
            return;
        }

        getTasksByDateRange(startingDate, endingDate)
                .thenAccept(tasks -> {
                    Platform.runLater(() -> {
                        if (tasks.isEmpty()) {
                            Utils.showAlert(Alert.AlertType.INFORMATION, "No tasks found", "No tasks found", "No tasks found for the selected date range.").showAndWait();
                            return;
                        }

                        tasksByDateRange = tasks;

                        String dest = "reports/PaymentReport_" + startingDate + "_" + endingDate + ".pdf";
                        PdfCreator.generatePaymentReport(dest, startingDate, endingDate, tasksByDateRange);
                        Utils.showAlert(Alert.AlertType.INFORMATION, "Report generated", "Report generated", "Report generated successfully.").showAndWait();
                        ((Stage) btnCancel.getScene().getWindow()).close();
                    });
                });

        String dest = "reports/PaymentReport_" + startingDate + "_" + endingDate + ".pdf";
        List<Trabajo> tasksByDateRange = getTasksByDateRange(startingDate, endingDate).join();

        PdfCreator.generatePaymentReport(dest, startingDate, endingDate, tasksByDateRange);

    }

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
