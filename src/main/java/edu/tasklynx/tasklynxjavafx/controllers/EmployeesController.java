package edu.tasklynx.tasklynxjavafx.controllers;

import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class EmployeesController implements Initializable {
    @FXML
    private TableView<Trabajador> tbvEmployees;
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Button btnAdd;
    @FXML
    private Label lblDetail;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSurname;
    @FXML
    private Label lblDni;
    @FXML
    private Label lblSpeciality;
    @FXML
    private Label lblEmail;

    private List<Trabajador> list;
    private Gson gson;
    private boolean showDetail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gson = new Gson();

        detailContainer.getChildren().remove(blockDetail);
        showDetail = false;

        loadEmployees();
    }

    public void onSelectedRow(MouseEvent mouseEvent) {
        showTaskDetail();
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        showTaskDetail();
    }

    public void showTaskDetail() {
        if (!showDetail) {
            detailContainer.setAlignment(Pos.TOP_CENTER);
            detailContainer.getChildren().add(blockDetail);
            showDetail = true;
        }

        Trabajador trabajador = tbvEmployees.getSelectionModel().getSelectedItem();
        if (trabajador != null) {
            lblDetail.setText(trabajador.getNombre() + " " + trabajador.getApellidos() + " - Detail");
            lblName.setText(trabajador.getNombre());
            lblSurname.setText(trabajador.getApellidos());
            lblDni.setText(trabajador.getDni());
            lblSpeciality.setText(trabajador.getEspecialidad());
            lblEmail.setText(trabajador.getEmail());
        }
    }

    private void loadEmployees() {
        String url = ServiceUtils.SERVER + "/employees";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> tbvEmployees.getItems().setAll(response.getEmployees()));
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Trabajador> getEmployeeById(String id) {
        String url = ServiceUtils.SERVER + "/employees/" + id;
        return ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorResponse.class))
                .thenApply(response -> {
                    if (response != null && !response.isError()) {
                        return response.getEmployee();
                    } else {
                        System.out.println("ERROR OBTENIENDO EMPLEADO 1: " + response.getErrorMessage());
                        return null;
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO EMPLEADO 2: " + ex.getMessage());
                    return null;
                });
    }}
