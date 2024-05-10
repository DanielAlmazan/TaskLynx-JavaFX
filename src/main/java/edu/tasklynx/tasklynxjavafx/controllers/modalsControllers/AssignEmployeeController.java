package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.controllers.TasksController;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AssignEmployeeController implements Initializable {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblCategoria;
    @FXML
    private ChoiceBox<Trabajador> cbEmployees;
    @FXML
    private Button btnConfirm;

    private Gson gson;
    private final String messageError = "There are no employees specialized in";
    private Trabajo trabajo;
    private Trabajador trabajadorSeleccionado;

    public void setTrabajo(Trabajo t) {
        trabajo = t;
        lblCategoria.setText(trabajo.getCategoria());
        getTrabajadoresByEspecialidad();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnConfirm.setDisable(true);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        // Listener para obtener el nuevo valor
        cbEmployees.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            trabajadorSeleccionado = cbEmployees.getItems().get((Integer) newValue);
            btnConfirm.setDisable(false);
        });
    }

    @FXML
    public void closeModal() {
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    @FXML
    public void confirmModal(ActionEvent event) {
        if (trabajadorSeleccionado != null) {
            trabajo.setId_trabajador(trabajadorSeleccionado);

            TasksController.trabajosToConfirm.add(trabajo);

            closeModal();
            /*String data = gson.toJson(trabajo);

            String url = ServiceUtils.SERVER + "/trabajos/" + trabajo.getCodTrabajo();
            ServiceUtils.getResponseAsync(url, data, "PUT")
                    .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                    .thenAccept(response -> Platform.runLater(() -> {
                        if(!response.isError()) {
                            Utils.showAlert(
                                    Alert.AlertType.CONFIRMATION,
                                    "Information",
                                    "Employee assigned",
                                    "The employee was assigned successfully"
                            ).showAndWait();
                            closeModal();
                        } else {
                            Utils.showAlert(
                                    Alert.AlertType.ERROR,
                                    "Error",
                                    "Error assign the employee",
                                    "An error ocurred while assigning the employee"
                            ).showAndWait();
                        }
                    }));*/
        }
    }

    private void getTrabajadoresByEspecialidad() {
        String urlEmployees = ServiceUtils.SERVER + "/trabajadores/especialidad/" +
                Utils.removeInvalidCharacters(trabajo.getCategoria());
        ServiceUtils.getResponseAsync(urlEmployees, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.getEmployees().isEmpty()) {
                            lblTitle.setText(messageError);
                            cbEmployees.setDisable(true);
                        } else
                            cbEmployees.getItems().setAll(response.getEmployees());
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }
}
