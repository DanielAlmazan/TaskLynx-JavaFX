package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.controllers.modalsControllers.AssignEmployeeController;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.EmailSender;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class TasksController implements Initializable {
    @FXML
    private VBox panelAssigments;
    @FXML
    private Label lblAssignments;
    @FXML
    private Button btnConfirmTasks;
    @FXML
    private TableView<Trabajo> tbvTasksToConfirm;
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
    private EmailSender emailSender;
    public static List<Trabajo> trabajosToConfirm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblAssignments.setText("There are no assignments to confirm");
        panelAssigments.setAlignment(Pos.CENTER);
        panelAssigments.getChildren().remove(tbvTasksToConfirm);
        btnConfirmTasks.setVisible(false);

        trabajosToConfirm = new ArrayList<>();

        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        addImages();
        toggleDetailView();
        loadTasks();
    }

    @FXML
    public void assignEmployee(ActionEvent actionEvent) {
        Trabajo trabajo = tbvTasks.getSelectionModel().getSelectedItem();

        if (trabajo != null) {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/assign-employee.fxml")));
            Stage modal = Utils.showModal(view, actionEvent);
            modal.setOnHidden((e) -> Platform.runLater(() -> {
                if (!trabajosToConfirm.isEmpty()) {
                    lblAssignments.setText("Assigned Tasks");
                    panelAssigments.setAlignment(Pos.TOP_CENTER);
                    panelAssigments.getChildren().add(tbvTasksToConfirm);
                    btnConfirmTasks.setVisible(true);
                    tbvTasksToConfirm.getItems().setAll(trabajosToConfirm);
                }
            }));
            modal.show();
            ((AssignEmployeeController) view.getController()).setTrabajo(trabajo);
        }
    }

    @FXML
    public void openNewTaskModal(ActionEvent actionEvent) {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newTaskModal.fxml")));
        Utils.showModal(view, actionEvent).showAndWait();
        loadTasks();
    }

    @FXML
    public void confirmAssignments(ActionEvent actionEvent) {
        System.out.println("PRUEBA");

        /*trabajosToConfirm.forEach(trabajo -> {
            String url = ServiceUtils.SERVER + "/trabajos/" + trabajo.getCodTrabajo();
            String data = gson.toJson(trabajo);

            ServiceUtils.getResponseAsync(url, data, "PUT")
                    .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                    .exceptionally(ex -> {
                        System.out.println("Error actualizando trabajo: " + ex.getMessage());
                        return null;
                    });

        });*/
    }

    private void addImages() {
        URL linkAdd = getClass().getResource("/icons/btn-add.png");

        Image iconAdd = new Image(linkAdd.toString(), 24, 24, false, true);

        btnAdd.setGraphic(new ImageView(iconAdd));
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

            btnAssignEmployee.setDisable(trabajo.getIdTrabajador() != null);
        } else {
            lblDetail.setText("Select a task to see his details");
            detailContainer.setAlignment(Pos.CENTER);
            detailContainer.getChildren().remove(blockDetail);
        }
    }

    private void loadTasks() {
        tbvTasks.getSelectionModel().clearSelection();
        String url = ServiceUtils.SERVER + "/trabajos/pendientes";
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
}
