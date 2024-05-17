package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.PdfCreator;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportsControllers {
    @FXML
    public Label lblEndingDate;
    @FXML
    public Label lblTimeSpent;
    @FXML
    private TableView<Trabajo> tbvTasks;
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Label lblDetail;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblStartingDate;
    @FXML
    private Label lblResponsible;
    @FXML
    public Button employeesWithoutTasksBtn;
    @FXML
    public Button employeesPayrollBtn;
    @FXML
    public Button generalReportBtn;
    @FXML
    public Button paymentsForDateBtn;

    Gson gson;

    public void initialize() {

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        toggleDetailView();
        loadCompletedasks();
        System.out.println("Tareas completadas: " + tbvTasks.getItems());
    }

    private void loadCompletedasks() {
        tbvTasks.getSelectionModel().clearSelection();
        String url = ServiceUtils.SERVER + "/trabajos/completados";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> tbvTasks.getItems().setAll(response.getJobs()));
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
    }

    public void onSelectedRow(MouseEvent mouseEvent) {
        toggleDetailView();
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        toggleDetailView();
    }

    public void toggleDetailView() {
        Trabajo trabajo = tbvTasks.getSelectionModel().getSelectedItem();

        if (trabajo != null) {
            if (!detailContainer.getChildren().contains(blockDetail)) {
                detailContainer.setAlignment(Pos.TOP_CENTER);
                detailContainer.getChildren().add(blockDetail);
            }

            lblDetail.setText(trabajo.getCodTrabajo() + " - Detail");
            lblDescription.setText(trabajo.getDescripcion());
            lblStartingDate.setText(trabajo.getFecIni().toString());
            lblEndingDate.setText(trabajo.getFecFin().toString());
            lblTimeSpent.setText((trabajo.getTiempo() + " hours"));
            lblResponsible.setText(trabajo.getNombreTrabajador());

        } else {
            lblDetail.setText("Select a task to see his details");
            detailContainer.setAlignment(Pos.CENTER);
            detailContainer.getChildren().remove(blockDetail);
        }
    }

    public List<Trabajador> getEmployeesWithoutTasks() {
        List<Trabajador> employeesWithoutTasks = new ArrayList<>();
        String url = ServiceUtils.SERVER + "/trabajadores/sintrabajospendientes";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        employeesWithoutTasks.addAll(response.getEmployees());
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
        return employeesWithoutTasks;
    }

    public List<Trabajador> getEmployees() {
        List<Trabajador> employees = new ArrayList<>();
        String url = ServiceUtils.SERVER + "/trabajadores";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        employees.addAll(response.getEmployees());
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
        return employees;
    }


    // Generating reports buttons
    public void onEmployeesWithoutTasksBtn(ActionEvent actionEvent) {
        List<Trabajador> employeesWithoutTasks = getEmployeesWithoutTasks();

        try {
            PdfCreator.createEmployeesWithoutTasksReport(employeesWithoutTasks);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Report generated", "Report generated", "The report has been created successfully").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEmployeesPayrollBtn(ActionEvent actionEvent) {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newPayrollModal.fxml")));
        Utils.showModal(view, (Stage) tbvTasks.getScene().getWindow()).showAndWait();
    }

    public void onGeneralReportBtn(ActionEvent actionEvent) {
        List<Trabajador> employees = getEmployees();

        try {
            PdfCreator.generateGeneralReport(employees);
            Utils.showAlert(Alert.AlertType.INFORMATION, "Report generated", "Report generated", "The report has been created successfully").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPaymentsForDateBtn(ActionEvent actionEvent) {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/paymentsDateModal.fxml")));
        Utils.showModal(view, (Stage) tbvTasks.getScene().getWindow()).showAndWait();
    }

}
