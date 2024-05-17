package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.TaskLynxController;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TasksController implements Initializable {
    @FXML
    private TableColumn<Trabajo, String> pruebasion;
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
    public static boolean employeeAssigned;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblAssignments.setText("There are no assignments to confirm");
        panelAssigments.setAlignment(Pos.CENTER);
        panelAssigments.getChildren().remove(tbvTasksToConfirm);
        btnConfirmTasks.setVisible(false);

        trabajosToConfirm = new ArrayList<>();
        employeeAssigned = false;

        pruebasion.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);

                    Trabajo t = getTableRow().getItem();

                    if (t.getPrevisualizar()) {
                        getStyleClass().add("assigned");
                    }
                }
            }
        });

        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        addImages();
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
                toggleAssigments();
                previewEmployee();
            }));
            modal.setOnCloseRequest((e) -> employeeAssigned = false);
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
        AtomicInteger completedServices = new AtomicInteger(0);
        AtomicInteger totalServices = new AtomicInteger(trabajosToConfirm.size());
        Lock lock = new ReentrantLock();

        trabajosToConfirm.forEach(trabajo -> {
            String url = ServiceUtils.SERVER + "/trabajos/" + trabajo.getCodTrabajo();
            String data = gson.toJson(trabajo);

            ServiceUtils.getResponseAsync(url, data, "PUT")
                    .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                    .thenAccept((response) -> {
                        // Bloqueo para modificar el valor del atomico
                        lock.lock();

                        completedServices.getAndIncrement();

                        if(completedServices.get() == totalServices.get()) {
                            trabajosToConfirm.clear();
                            Platform.runLater(() -> {
                                loadTasks();
                                toggleAssigments();

                                Utils.showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Information",
                                        "Employees assigned",
                                        "The employees was assigned successfully"
                                ).show();
                            });
                        }

                        // Desbloqueo para poder acceder al valor del atomico de nuevo
                        lock.unlock();
                    })
                    .exceptionally(ex -> {
                        System.out.println("Error actualizando trabajo: " + ex.getMessage());
                        return null;
                    });

        });
    }

    private void previewEmployee() {
        if(!trabajosToConfirm.isEmpty() && employeeAssigned) {
            tbvTasks.getSelectionModel().getSelectedItem().setId_trabajador(
                    trabajosToConfirm.getLast().getIdTrabajador()
            );
            btnAssignEmployee.setDisable(true);
            lblResponsible.setText(trabajosToConfirm.getLast().getNombreTrabajador());
            tbvTasks.getSelectionModel().getSelectedItem().setPrevisualizar(true);

            tbvTasks.refresh();
        }
    }

    private void toggleAssigments() {
        if (!trabajosToConfirm.isEmpty() && !btnConfirmTasks.isVisible()) {
            lblAssignments.setText("Assigned Tasks");
            panelAssigments.setAlignment(Pos.TOP_CENTER);
            panelAssigments.getChildren().add(tbvTasksToConfirm);
            btnConfirmTasks.setVisible(true);
            tbvTasksToConfirm.getItems().setAll(trabajosToConfirm);
            TaskLynxController.pendingChanges = true;
        } else if (!trabajosToConfirm.isEmpty()) {
            tbvTasksToConfirm.getItems().setAll(trabajosToConfirm);
            TaskLynxController.pendingChanges = true;
        } else {
            panelAssigments.setAlignment(Pos.CENTER);
            panelAssigments.getChildren().remove(tbvTasksToConfirm);
            btnConfirmTasks.setVisible(false);
            lblAssignments.setText("There are no assignments to confirm");
            TaskLynxController.pendingChanges = false;
        }
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

        toggleDetailView();
    }
}
