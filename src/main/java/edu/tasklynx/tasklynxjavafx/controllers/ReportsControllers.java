package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Objects;

public class ReportsControllers {
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
    public Button employeesHistoryBtn;
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

            lblDetail.setText(trabajo.getCategoria() + " - Detail");
            lblDescription.setText(trabajo.getDescripcion());
            lblStartingDate.setText(trabajo.getFecIni().toString());
            lblResponsible.setText(trabajo.getNombreTrabajador());

        } else {
            lblDetail.setText("Select a task to see his details");
            detailContainer.setAlignment(Pos.CENTER);
            detailContainer.getChildren().remove(blockDetail);
        }
    }

    public void onEmployeesPayrollBtn(ActionEvent actionEvent) {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newPayrollModal.fxml")));
        Utils.showModal(view, actionEvent).showAndWait();
    }

    public void onEmployeesWithoutTasksBtn(ActionEvent actionEvent) {}
    public void onEmployeesHistoryBtn(ActionEvent actionEvent) {}
    public void onGeneralReportBtn(ActionEvent actionEvent) {}
    public void onPaymentsForDateBtn(ActionEvent actionEvent) {}

}
