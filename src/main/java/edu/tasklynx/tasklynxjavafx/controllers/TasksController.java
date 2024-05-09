package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TasksController implements Initializable {
    @FXML
    private TableView<Trabajo> tbvTasks;
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnAssignEmployee;
    @FXML
    private Label lblDetail;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblStartingDate;
    @FXML
    private Label lblResponsible;

    private Gson gson;
    private boolean showDetail;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        detailContainer.getChildren().remove(blockDetail);
        showDetail = false;

        addImages();
        gson = new Gson();

        loadTasks();
    }

    @FXML
    public void addTask(ActionEvent actionEvent) {
        // TODO
    }

    private void addImages() {
        URL linkAdd = getClass().getResource("/icons/btn-add.png");

        Image iconAdd = new Image(linkAdd.toString(), 24, 24, false, true);

        btnAdd.setGraphic(new ImageView(iconAdd));
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

        Trabajo trabajo = tbvTasks.getSelectionModel().getSelectedItem();
        if (trabajo != null) {
            lblDetail.setText(trabajo.getCategoria() + " - Detail");
            lblDescription.setText(trabajo.getDescripcion());
            lblStartingDate.setText(trabajo.getFec_ini().toString());
            lblResponsible.setText(trabajo.getNombre_trabajador());

            btnAssignEmployee.setDisable(trabajo.getId_trabajador() == null);
        }
    }

    private void loadTasks() {
        String url = ServiceUtils.SERVER + "/trabajos/pendientes";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenApply(response -> {
                    if (!response.isError()) {
//                        Platform.runLater(() -> tbvTasks.getItems().setAll(response.getJobs()));
                        return response.getJobs();
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                        return null;
                    }
                })
                .thenAccept(list -> {
                    if (list != null) {
                        list.forEach(task -> {
                            if (task.getId_trabajador() != null) {
                                String urlEmployee = ServiceUtils.SERVER + "/trabajadores/" + task.getId_trabajador();
                                ServiceUtils.getResponseAsync(urlEmployee, null, "GET")
                                        .thenApply(json -> gson.fromJson(json, TrabajadorResponse.class))
                                        .thenAccept(response -> task.setNombre_trabajador(response.getEmployee().getNombre() +
                                                " " + response.getEmployee().getApellidos()))
                                        .thenAccept(r -> Platform.runLater(() -> tbvTasks.getItems().setAll(list)))
                                        .exceptionally(ex -> {
                                            System.out.println("ERROR: " + ex.getMessage());
                                            return null;
                                        });
                            }
                        });
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
    }
}
