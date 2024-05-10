package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.net.URL;
import java.util.ResourceBundle;

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
    public Button btnCreate;
    @FXML
    public Button btnCancel;
    // endregion

    private Gson gson;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbPrioridad.getItems().addAll(1, 2, 3, 4);
        dpFecIni.setValue(LocalDate.now());
    }

    public void onCreateTaskBtn() {
        if (!checkFields()) {
            try {
                Trabajo newTask = getTaskFromModal();
                submitTask(newTask);
            } catch (NullPointerException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Error: Some fields are not correct");
        }
    }

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

    private boolean checkFields() {
        lblErrCodTrabajo.setText(checkCodTrabajo(tiCodTrabajo.getText()));
        lblErrCategoria.setText(checkCategoria(tiCategoria.getText()));
        lblErrDescripcion.setText(checkDescripcion(tiDescripcion.getText()));
        lblErrPrioridad.setText(checkPrioridad(cbPrioridad.getValue()));
        lblErrIdTrabajador.setText(checkIdTrabajador(cbIdTrabajador.getValue()));

        lblErrCodTrabajo.setVisible(lblErrCodTrabajo.getText() != null);
        lblErrCategoria.setVisible(lblErrCategoria.getText() != null);
        lblErrDescripcion.setVisible(lblErrDescripcion.getText() != null);
        lblErrPrioridad.setVisible(lblErrPrioridad.getText() != null);
        lblErrIdTrabajador.setVisible(lblErrIdTrabajador.getText() != null);

        return lblErrCodTrabajo.isVisible()
                && lblErrCategoria.isVisible()
                && lblErrDescripcion.isVisible()
                && lblErrPrioridad.isVisible()
                && lblErrIdTrabajador.isVisible();
    }

    private String checkCodTrabajo(String codTrabajo) {
        String error = null;
        if (codTrabajo == null || codTrabajo.isEmpty()) {
            error = "El código de trabajo es obligatorio";
        } else if (codTrabajo.length() > 5) {
            error = "El código de trabajo no puede tener más de 5 caracteres";
        } else /* if (SomeController.findById(codTrabajo) != null */ {
            // error = "El código de trabajo ya existe";
        }

        return error;
    }

    private String checkCategoria(String categoria) {
        String error = null;
        if (categoria == null || categoria.isEmpty()) {
            error = "La categoría es obligatoria";
        } else if (categoria.length() > 50) {
            error = "La categoría no puede tener más de 50 caracteres";
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

    private String checkIdTrabajador(Trabajador idTrabajador) {
        String error = "";
        // TODO: Implement call to /trabajadores/{id} endpoint for checking if it exists
        if (idTrabajador != null /* && SomeController.findById(idTrabajador) */) {
            error = "El trabajador no existe";
        }

        return error;
    }

    private void submitTask(Trabajo newTask) {
        String url = ServiceUtils.SERVER + "/trabajos";
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        String data = gson.toJson(newTask);

        ServiceUtils.getResponseAsync(url, data, "POST")
                .thenApply(json -> gson.fromJson(json, TrabajoResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            // Show a success alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setHeaderText("Employee added.");
                            alert.setContentText("The employee was added successfully.");
                            alert.showAndWait();
                            onCancelBtn();
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Show an error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error adding the employee");
                            alert.setContentText(response.getErrorMessage());
                            alert.showAndWait();
                        });
                    }
                });
    }
}
