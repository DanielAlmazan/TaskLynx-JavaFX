package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Payroll;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewPayrollController {

    @FXML
    public TableView<Trabajo> tbvCompletedTasks;
    @FXML
    public Label totalAmount;
    @FXML
    public Label tvName;
    @FXML
    public Label tvSurname;
    @FXML
    public Label tvDNI;
    @FXML
    public Label tvEmail;
    @FXML
    public Label tvSpeciality;
    @FXML
    public ChoiceBox<Trabajador> cbTrabajador;
    @FXML
    public Button btnAddEmployee;
    @FXML
    public Button btnCancel;
    @FXML
    public DatePicker dpFecIni;
    @FXML
    public DatePicker dpFecFin;


    private Trabajador trabajadorSeleccionado;
    private Gson gson;
    private List<Trabajo> tasksByDateRange;

    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        getTrabajadores();
        cbTrabajador.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            trabajadorSeleccionado = newValue;
            setEmployeeData();
            setEmployeeTasks();
        });
    }

    public void setEmployeeData() {
        Trabajador trabajadorSeleccionado = cbTrabajador.getSelectionModel().getSelectedItem();

        tvName.setText(trabajadorSeleccionado.getNombre());
        tvSurname.setText(trabajadorSeleccionado.getApellidos());
        tvDNI.setText(trabajadorSeleccionado.getDni());
        tvEmail.setText(trabajadorSeleccionado.getEmail());
        tvSpeciality.setText(trabajadorSeleccionado.getEspecialidad());
    }

    public void setEmployeeTasks() {
        String url = ServiceUtils.SERVER + "/trabajadores/" + trabajadorSeleccionado.getIdTrabajador() + "/trabajos/completados";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.getJobs().isEmpty()) {
                            tbvCompletedTasks.setDisable(true);
                        } else {
                            tbvCompletedTasks.getItems().setAll(response.getJobs());
                            totalAmount.setText("Total salary: " + response.getJobs().stream().mapToDouble(Trabajo::getRemuneration).sum() + "€");
                        }
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }

    public CompletableFuture<List<Trabajo>> getEmployeeTasksByDateRange(LocalDate startingDate, LocalDate endingDate) {
        String url = ServiceUtils.SERVER + "/trabajadores/" + trabajadorSeleccionado.getIdTrabajador() + "/trabajos/completados";

        url += "?fechaIni=" + startingDate + "&fechaFin=" + endingDate;

        return ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenApply(response -> response.getJobs())
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return List.of();
                });
    }

    public void onGeneratePayrollBtn() {
        LocalDate startingDate = dpFecIni.getValue();
        LocalDate endingDate = dpFecFin.getValue();

        if (startingDate == null || endingDate == null) {
            Utils.showAlert(Alert.AlertType.ERROR, "Invalid date range", "Invalid date range", "Please select a valid date range.").showAndWait();
            return;
        }

        getEmployeeTasksByDateRange(startingDate, endingDate)
                .thenAccept(tasks -> {
                    Platform.runLater(() -> {
                        if (tasks.isEmpty()) {
                            Utils.showAlert(Alert.AlertType.INFORMATION, "No tasks found", "No tasks found", "No tasks found for the selected date range.").showAndWait();
                            return;
                        }

                        tasksByDateRange = tasks;

                        double time = tasksByDateRange.stream().mapToDouble(Trabajo::getTiempo).sum();
                        double salary = tasksByDateRange.stream().mapToDouble(Trabajo::getRemuneration).sum();

                        Payroll payroll = new Payroll(trabajadorSeleccionado, tasksByDateRange, startingDate, endingDate, time, salary);

                        String dest = "reports/payroll/payroll_" + trabajadorSeleccionado.getNombre() + "-" + trabajadorSeleccionado.getApellidos()
                                + "_" + startingDate + "_" + endingDate + ".pdf";
                        
                        File payrollFile = PdfCreator.createPayrollPDF(payroll, dest);
                        
                        EmailSender emailSender = new EmailSender();

                        try {
                            emailSender.sendPayrollEmail(trabajadorSeleccionado, payrollFile);
                            Utils.showAlert(Alert.AlertType.INFORMATION, "Payroll generated", "Payroll generated", "Payroll generated successfully.").show();
                        } catch (Exception e) {
                            Utils.showAlert(Alert.AlertType.ERROR, "Error", "Error", "Error sending payroll email.").showAndWait();
                        }
                        
                        ((Stage) btnCancel.getScene().getWindow()).close();
                    });
                });
    }

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private void getTrabajadores() {
        String urlEmployees = ServiceUtils.SERVER + "/trabajadores";
        ServiceUtils.getResponseAsync(urlEmployees, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.getEmployees().isEmpty()) {
                            cbTrabajador.setDisable(true);
                        } else
                            cbTrabajador.getItems().setAll(response.getEmployees());
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }

}
