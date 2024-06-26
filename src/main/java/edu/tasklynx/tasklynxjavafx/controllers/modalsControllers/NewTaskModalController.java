package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Habitacion;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.HabitacionResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoResponse;
import edu.tasklynx.tasklynxjavafx.utils.EmailSender;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;

import jakarta.mail.MessagingException;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.net.URL;
import java.util.*;

public class NewTaskModalController implements Initializable {
    // region FXML attributes
    @FXML
    public TextField tiCodTrabajo;
    @FXML
    public TextField tiCategoria;
    @FXML
    public TextField tiDescripcion;
    @FXML
    public DatePicker dpFecIni;
    @FXML
    public ChoiceBox<Trabajador> cbIdTrabajador;
    @FXML
    public ChoiceBox<Integer> cbPrioridad;
    @FXML
    public Label lblTitle;
    @FXML
    public Label lblErrCodTrabajo;
    @FXML
    public Label lblErrCategoria;
    @FXML
    public Label lblErrDescripcion;
    @FXML
    public Label lblErrPrioridad;
    @FXML
    public Label lblErrIdTrabajador;
    @FXML
    public Label lblErrRoom;
    @FXML
    public Button btnCreate;
    @FXML
    public Button btnCancel;
    @FXML
    public CheckBox chkBoxIsCleaning;
    @FXML
    public ChoiceBox<Habitacion> cbRoom;
    // endregion

    private Trabajo editingTask;

    private List<Trabajador> employees;
    private Gson gson;

    public void setEditMode(Trabajo task) {
        if (task != null) {
            lblTitle.setText("Edit task");
            btnCreate.setText("Update");
            editingTask = task;

            tiCodTrabajo.setText(task.getCodTrabajo());
            tiCodTrabajo.setDisable(true);

            tiDescripcion.setText(task.getDescripcion());
            if (task.getCategoria().equalsIgnoreCase("limpieza")) {
                tiDescripcion.setDisable(true);
                chkBoxIsCleaning.setSelected(true);

                String[] description = task.getDescripcion().split(" ");
                int roomNumber = Integer.parseInt(description[description.length - 1]);
                cbRoom.setValue(new Habitacion(roomNumber));
                cbRoom.setDisable(false);

                System.out.println("Description: " + description);
                System.out.println("Room: " + roomNumber);
            }
            chkBoxIsCleaning.setDisable(true);

            tiCategoria.setText(task.getCategoria());
            tiCategoria.setDisable(true);

            dpFecIni.setValue(task.getFecIni());
            cbPrioridad.setValue(task.getPrioridad());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbPrioridad.getItems().addAll(1, 2, 3, 4);
        dpFecIni.setValue(LocalDate.now());

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        getEmployees();
        getDirtyRooms();
    }

    // region FXML methods
    @FXML
    public void onCreateTaskBtn() {
        if (!fieldsAreValid()) {
            try {
                Trabajo newTask = getTaskFromModal();
                submitTask(newTask);
            } catch (NullPointerException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Some fields are not correct");
        }
    }

    @FXML
    public void onCheckBoxCleaningClicked() {
        if (chkBoxIsCleaning.isSelected()) {
            cbRoom.setDisable(false);
            tiCategoria.setText("Limpieza");
            tiCategoria.setDisable(true);
        } else {
            cbRoom.setDisable(true);
            tiCategoria.setDisable(false);
            tiCategoria.setText(null);
            tiDescripcion.setText(null);
        }
        filterEmployeesByCategory();
        setDescriptionForRoom();
    }

    @FXML
    public void onRoomSelected() {
        setDescriptionForRoom();
    }

    @FXML
    public void onCategoriaKeyReleased() {
        filterEmployeesByCategory();
    }

    @FXML
    public void onTrabajadorSelected() {
        if (cbIdTrabajador.getValue() != null) {
            tiCategoria.setText(cbIdTrabajador.getValue().getEspecialidad());
        }
    }
    // endregion

    // region Validation methods

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private Trabajo getTaskFromModal() {
        return new Trabajo(
                tiCodTrabajo.getText(),
                tiCategoria.getText(),
                tiDescripcion.getText(),
                dpFecIni.getValue(),
                null,
                null,
                cbPrioridad.getValue() != null ? cbPrioridad.getValue() : 1,
                cbIdTrabajador.getValue()
        );
    }

    private boolean fieldsAreValid() {
        lblErrCodTrabajo.setText(checkCodTrabajo(tiCodTrabajo.getText()));
        lblErrCategoria.setText(checkCategoria(tiCategoria.getText()));
        lblErrRoom.setText(checkRoom(cbRoom.getValue()));
        lblErrDescripcion.setText(checkDescripcion(tiDescripcion.getText()));
        lblErrPrioridad.setText(checkPrioridad(cbPrioridad.getValue()));

        lblErrCodTrabajo.setVisible(lblErrCodTrabajo.getText() != null);
        lblErrCategoria.setVisible(lblErrCategoria.getText() != null);
        lblErrRoom.setVisible(lblErrRoom.getText() != null);
        lblErrDescripcion.setVisible(lblErrDescripcion.getText() != null);
        lblErrPrioridad.setVisible(lblErrPrioridad.getText() != null);

        return lblErrCodTrabajo.isVisible()
               || lblErrRoom.isVisible()
               || lblErrCategoria.isVisible()
               || lblErrDescripcion.isVisible()
               || lblErrPrioridad.isVisible();
    }

    private String checkCodTrabajo(String codTrabajo) {
        String error = null;
        if (editingTask == null) {
            if (codTrabajo == null || codTrabajo.isEmpty()) {
                error = "El código de trabajo es obligatorio";
            } else if (codTrabajo.length() > 5) {
                error = "El código de trabajo no puede tener más de 5 caracteres";
            } else if (codTrabajoExists(codTrabajo)) {
                error = "El código de trabajo ya existe";
            }
        }

        return error;
    }

    private boolean codTrabajoExists(String codTrabajo) {
        String url = ServiceUtils.SERVER + "/trabajos/" + codTrabajo;

        return ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                .thenApply(response -> response != null && response.getJob() != null)
                .join();
    }

    private String checkCategoria(String categoria) {
        String error = null;
        if (categoria == null || categoria.isEmpty()) {
            error = "La categoría es obligatoria";
        } else if (categoria.length() > 50) {
            error = "La categoría no puede tener más de 50 caracteres";
        } else if (cbIdTrabajador.getValue() != null && !cbIdTrabajador.getValue().getEspecialidad().toLowerCase().contains(categoria.toLowerCase())) {
            error = "La categoría debe ser la misma que la del trabajador";
        }

        return error;
    }

    private String checkRoom(Habitacion room) {
        String error = null;
        if (chkBoxIsCleaning.isSelected() && room == null) {
            error = "Si la tarea es de limpieza, debe seleccionar una habitación";
        }

        return error;
    }

    private String checkDescripcion(String descripcion) {
        String error = null;
        if (descripcion == null || descripcion.isEmpty()) {
            error = "La descripción es obligatoria";
        } else if (descripcion.length() > 500) {
            error = "La descripción no puede tener más de 500 caracteres";
        }

        return error;
    }

    private String checkPrioridad(Integer prioridad) {
        String error = null;
        if (prioridad == null) {
            error = "La prioridad es obligatoria";
        } else if (prioridad < 1 || prioridad > 4) {
            error = "La prioridad debe estar entre 1 y 4";
        }

        return error;
    }
    // endregion

    // region Aux methods
    private void setDescriptionForRoom() {
        tiDescripcion.setDisable(false);

        if (chkBoxIsCleaning.isSelected()) {
            tiDescripcion.setText(null);

            if (cbRoom.getValue() != null) {
                tiDescripcion.setText("Limpieza de la habitación " + cbRoom.getValue().getNumero());
            }

            tiDescripcion.setDisable(true);
        }
    }

    private void filterEmployeesByCategory() {
        cbIdTrabajador.getItems().clear();

        if (tiCategoria.getText() == null
            || tiCategoria.getText().isBlank()
            || tiCategoria.getText().isEmpty()
        ) {
            cbIdTrabajador.getItems().addAll(employees);
        } else {
            tiCategoria.setDisable(false);
            cbIdTrabajador.getItems()
                    .addAll(employees.stream()
                            .filter(e ->
                                    Utils.removeInvalidCharacters(e.getEspecialidad())
                                            .toLowerCase().contains(
                                                    Utils.removeInvalidCharacters(tiCategoria.getText()).toLowerCase()
                                            ))
                            .toList()
                    );
            if (editingTask != null) {
                tiCategoria.setDisable(true);
            }
        }
    }
    // endregion

    // region Service methods
    private void submitTask(Trabajo newTask) {
        String url = ServiceUtils.SERVER + "/trabajos";

        String data = gson.toJson(newTask);

        if (editingTask != null) {
            url += "/" + editingTask.getCodTrabajo();
        }

        ServiceUtils.getResponseAsync(url, data, editingTask == null ? "POST" : "PUT")
                .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            // Show a success alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            String successMessage = editingTask == null ? "The task was added successfully." : "The task was updated successfully.";
                            
                            if (response.getJob().getIdTrabajador() != null) {
                                sendEmail(response.getJob());
                            }
                            
                            alert.setHeaderText(successMessage);
                            alert.setContentText("The task was added successfully.");
                            alert.showAndWait();
                            onCancelBtn();
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Show an error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error adding the task");
                            alert.setContentText(response.getErrorMessage());
                            alert.showAndWait();
                        });
                    }
                });
    }

    private void getEmployees() {
        String url = ServiceUtils.SERVER + "/trabajadores";

        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            employees = response.getEmployees();
                            cbIdTrabajador.getItems().addAll(employees);
                            if (editingTask != null) {
                                filterEmployeesByCategory();
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Show an error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error getting the employees");
                            alert.setContentText(response.getErrorMessage());
                            alert.showAndWait();
                        });
                    }
                });
    }

    private void sendEmail(Trabajo justAssignedTrabajo) {
        EmailSender emailSender = new EmailSender();

        try {
            emailSender.sendTaskNotificationEmail(justAssignedTrabajo.getIdTrabajador(), List.of(justAssignedTrabajo));
        } catch (IOException | MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }


    private void getDirtyRooms() {
        String url = ServiceUtils.SERVER_NEST + "/limpieza/sucias";

        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, HabitacionResponse.class))
                .thenAccept(response -> {
                    if (response != null) {
                        Platform.runLater(() -> {
                            cbRoom.getItems()
                                    .addAll(response.getHabitaciones()
                                            .stream()
                                            .sorted(Comparator.comparingInt(Habitacion::getNumero))
                                            .toList()
                                    );
                            if (editingTask != null && !editingTask.getCategoria().equalsIgnoreCase("limpieza")) {
                                cbRoom.setDisable(true);
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Show an error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error getting the dirty rooms");
                            alert.setContentText("There was an error getting the dirty rooms");
                            alert.showAndWait();
                        });
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("Error getting dirty rooms: " + ex.getMessage());
                    return null;
                });
    }
    // endregion
}
