package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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

    public void setTrabajo(Trabajo t) {
        trabajo = t;
        lblCategoria.setText(trabajo.getCategoria());
        getTrabajadoresByEspecialidad();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnConfirm.setDisable(true);
        gson = new Gson();
    }

    @FXML
    public void closeModal(ActionEvent event) {
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private void getTrabajadoresByEspecialidad() {
        String urlEmployees = ServiceUtils.SERVER + "/employees";
        ServiceUtils.getResponseAsync(urlEmployees, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if(response.getEmployees().isEmpty()) lblTitle.setText(messageError);
                    else Platform.runLater(() -> cbEmployees.getItems().setAll(response.getEmployees()));
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }
}
