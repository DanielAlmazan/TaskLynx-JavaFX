package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NewPayrollController {

    @FXML
    public TableView<Trabajo> tbvCompletedTasks;
    @FXML
    public Label totalAmount;
    @FXML
    public Label tvName;
    @FXML
    public Label tvSurname;
    @FXML
    public Label tvDNI;
    @FXML
    public Label tvEmail;
    @FXML
    public Label tvSpeciality;
    @FXML
    public ChoiceBox<Trabajador> cbTrabajador;
    @FXML
    public Button btnAddEmployee;
    @FXML
    public Button btnCancel;

    private Trabajador trabajadorSeleccionado;
    private Gson gson;

    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        getTrabajadores();
        cbTrabajador.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            trabajadorSeleccionado = newValue;
            setEmployeeData();
            setEmployeeTasks();
        });
    }

    public void setEmployeeData() {
        Trabajador trabajadorSeleccionado = cbTrabajador.getSelectionModel().getSelectedItem();

        tvName.setText(trabajadorSeleccionado.getNombre());
        tvSurname.setText(trabajadorSeleccionado.getApellidos());
        tvDNI.setText(trabajadorSeleccionado.getDni());
        tvEmail.setText(trabajadorSeleccionado.getEmail());
        tvSpeciality.setText(trabajadorSeleccionado.getEspecialidad());
    }

    public void setEmployeeTasks() {
        String url = ServiceUtils.SERVER + "/trabajadores/" + trabajadorSeleccionado.getIdTrabajador() + "/trabajos/completados";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.getJobs().isEmpty()) {
                            tbvCompletedTasks.setDisable(true);
                        } else {
                            tbvCompletedTasks.getItems().setAll(response.getJobs());
                            totalAmount.setText("Total salary: " + response.getJobs().stream().mapToDouble(Trabajo::getRemuneration).sum() + "€");
                        }
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }

    public void onGeneratePayrollBtn() {
    }

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private void getTrabajadores() {
        String urlEmployees = ServiceUtils.SERVER + "/trabajadores";
        ServiceUtils.getResponseAsync(urlEmployees, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.getEmployees().isEmpty()) {
                            cbTrabajador.setDisable(true);
                        } else
                            cbTrabajador.getItems().setAll(response.getEmployees());
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR: " + ex.getMessage());
                    return null;
                });
    }

}
