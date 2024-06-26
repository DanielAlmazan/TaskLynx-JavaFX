package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.TaskLynxController;
import edu.tasklynx.tasklynxjavafx.controllers.modalsControllers.AssignEmployeeController;
import edu.tasklynx.tasklynxjavafx.controllers.modalsControllers.NewTaskModalController;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.BaseResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoResponse;
import edu.tasklynx.tasklynxjavafx.utils.EmailSender;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import jakarta.mail.MessagingException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class TasksController implements Initializable {
    public Button btnEditTask;
    @FXML
    private TableColumn<Trabajo, String> columnResponsible;
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
    @FXML
    private Label lblPriority;

    private Gson gson;
    private EmailSender emailSender;

    public static List<Trabajo> trabajosToConfirm;
    public static boolean employeeAssigned = false;
    public static Trabajo taskReassigned = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblAssignments.setText("There are no assignments to confirm");
        panelAssigments.setAlignment(Pos.CENTER);
        panelAssigments.getChildren().remove(tbvTasksToConfirm);
        btnConfirmTasks.setVisible(false);

        trabajosToConfirm = new ArrayList<>();

        columnResponsible.setCellFactory(column -> new TableCell<>() {
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
    public void onSelectedRow(MouseEvent mouseEvent) {
        toggleDetailView();

        if(mouseEvent.getButton().equals(MouseButton.PRIMARY) &&
                mouseEvent.getClickCount() == 2) {
            Trabajo trabajoSelected = tbvTasks.getSelectionModel().getSelectedItem();

            if(trabajoSelected != null) {
                if(trabajoSelected.getIdTrabajador() == null) {
                    assignEmployee();
                } else if (trabajoSelected.getPrevisualizar()) {
                    trabajoSelected.setPrevisualizar(false);
                    trabajoSelected.setId_trabajador(null);
                    lblResponsible.setText("");
                    btnEditTask.setDisable(false);

                    filterPendingTasks(trabajoSelected);
                    tbvTasks.refresh();
                }
            }
        }
    }

    @FXML
    public void onKeyReleased() {
        toggleDetailView();
    }

    @FXML
    public void assignEmployee() {
        Trabajo trabajo = tbvTasks.getSelectionModel().getSelectedItem();

        if (trabajo != null) {
            FXMLLoader view = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/assign-employee.fxml")));
            Stage modal = Utils.showModal(view, (Stage) tbvTasks.getScene().getWindow());
            modal.setOnHidden((e) -> Platform.runLater(() -> {
                if(taskReassigned != null) {
                    reassignEmployee(taskReassigned);
                }

                toggleAssigments();
                previewEmployee();
            }));
            modal.show();
            ((AssignEmployeeController) view.getController()).setTrabajo(trabajo);
        }
    }

    @FXML
    public void openNewTaskModal() {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newTaskModal.fxml")));
        Utils.showModal(view, (Stage) tbvTasks.getScene().getWindow()).showAndWait();
        loadTasks();
    }

    @FXML
    public void confirmAssignments() {
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
                            sendEmails(trabajosToConfirm);
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

    @FXML
    public void deleteTask() {
        Alert alert = Utils.showAlert(
                Alert.AlertType.CONFIRMATION,
                "Caution",
                "Are you sure to delete this task?",
                "This action can't be undone."
        );

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Trabajo trabajo = tbvTasks.getSelectionModel().getSelectedItem();

            if(trabajo != null) {
                String url = ServiceUtils.SERVER + "/trabajos/" + trabajo.getCodTrabajo();
                ServiceUtils.getResponseAsync(url, null, "DELETE")
                        .thenApply(json -> gson.fromJson(json, BaseResponse.class))
                        .thenAccept(response -> Platform.runLater(() -> {
                            if (!response.isError()) {
                                loadTasks();
                                filterPendingTasks(trabajo);
                                Utils.showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Success",
                                        "Task deleted sucessfully",
                                        "The task hass been removed from the application"
                                ).show();
                            } else {
                                Utils.showAlert(
                                        Alert.AlertType.ERROR,
                                        "Error",
                                        "Error deleting the task",
                                        response.getErrorMessage()
                                ).show();
                            }
                        }))
                        .exceptionally(ex -> {
                            System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                            return null;
                        });
            }
        }
    }

    @FXML
    public void onEditTask() {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newTaskModal.fxml")));
        Stage modal = Utils.showModal(view, (Stage) tbvTasks.getScene().getWindow());
        modal.setOnHiding((e) -> loadTasks());
        modal.show();
        
        ((NewTaskModalController) view.getController()).setEditMode(tbvTasks.getSelectionModel().getSelectedItem());
    }

    private void previewEmployee() {
        if(!trabajosToConfirm.isEmpty() && (employeeAssigned || taskReassigned != null)) {
            Trabajo trabajo = employeeAssigned ? trabajosToConfirm.getLast() : taskReassigned;

            tbvTasks.getSelectionModel().getSelectedItem().setId_trabajador(trabajo.getIdTrabajador());
            lblResponsible.setText(trabajo.getNombreTrabajador());
            tbvTasks.getSelectionModel().getSelectedItem().setPrevisualizar(true);
            btnEditTask.setDisable(true);

            tbvTasks.refresh();

            // Back to default values
            employeeAssigned = false;
            taskReassigned = null;
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

    private void toggleDetailView() {
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
            lblPriority.setText(Integer.toString(trabajo.getPrioridad()));

            btnAssignEmployee.setDisable(trabajo.getIdTrabajador() != null && !trabajo.getPrevisualizar());
            btnEditTask.setDisable(trabajo.getIdTrabajador() != null);
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
                        Platform.runLater(() -> {
                            HashSet<Trabajo> trabajos = new HashSet<>();

                            if(!trabajosToConfirm.isEmpty()) {
                                trabajos.addAll(trabajosToConfirm);
                                trabajos.forEach(t -> t.setPrevisualizar(true));
                            }

                            trabajos.addAll(response.getJobs());
                            tbvTasks.getItems().setAll(trabajos);
                        });
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

    private void sendEmails(List<Trabajo> justAssignedTrabajos) {
        HashMap<Trabajador, List<Trabajo>> trabajosByTrabajador = new HashMap<>();

        justAssignedTrabajos.forEach(trabajo -> {
            Trabajador trabajador = trabajo.getIdTrabajador();
            trabajosByTrabajador.putIfAbsent(trabajador, new ArrayList<>());
            trabajosByTrabajador.get(trabajador).add(trabajo);
        });

        EmailSender emailSender = new EmailSender();

        trabajosByTrabajador.forEach((trabajador, trabajos) -> {
            try {
                emailSender.sendTaskNotificationEmail(trabajador, trabajos);
            } catch (IOException | MessagingException e) {
                System.out.println("Error sending email: " + e.getMessage());
            }
        });
    }

    private void reassignEmployee(Trabajo trabajoReasigned) {
        if(!trabajosToConfirm.isEmpty()) {
            trabajosToConfirm.forEach(t -> {
                if(t.getCodTrabajo().equals(trabajoReasigned.getCodTrabajo())) {
                    t.setId_trabajador(trabajoReasigned.getIdTrabajador());
                }
            });

            tbvTasksToConfirm.getItems().setAll(trabajosToConfirm);
        }
    }

    private void filterPendingTasks(Trabajo trabajoRemoved) {
        trabajosToConfirm = trabajosToConfirm.stream()
                .filter(t -> !t.getCodTrabajo().equals(trabajoRemoved.getCodTrabajo()))
                .collect(Collectors.toList());

        toggleAssigments();
    }
}
