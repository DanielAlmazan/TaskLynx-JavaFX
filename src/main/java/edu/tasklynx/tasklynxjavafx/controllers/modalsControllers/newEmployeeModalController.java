package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class newEmployeeModalController {

    @FXML
    private TextField tiId;
    @FXML
    private TextField tiDni;
    @FXML
    private TextField tiName;
    @FXML
    private TextField tiSurname;
    @FXML
    private TextField tiPassword;
    @FXML
    private TextField tiSpeciality;
    @FXML
    private TextField tiEmail;
    @FXML
    private Button btnAddEmployee;
    @FXML
    private Button btnCancel;

    Gson gson;

    private void addEmployee() {

        // Get the data from the form
        String id = tiId.getText();
        String dni = tiDni.getText();
        String name = tiName.getText();
        String surname = tiSurname.getText();
        String password = tiPassword.getText();
        String speciality = tiSpeciality.getText();
        String email = tiEmail.getText();

        // Serialize the data to JSON
        String url = ServiceUtils.SERVER + "/trabajadores";
        String data = "{\n" +
                "  \"idTrabajador\": \"" + id + "\",\n" +
                "  \"dni\": \"" + dni + "\",\n" +
                "  \"nombre\": \"" + name + "\",\n" +
                "  \"apellidos\": \"" + surname + "\",\n" +
                "  \"especialidad\": \"" + speciality + "\",\n" +
                "  \"contraseña\": \"" + password + "\",\n" +
                "  \"email\": \"" + email + "\"\n" +
                "}";

        // Call to the service to add the new employee
        ServiceUtils.getResponseAsync(url, data, "POST")
                .thenApply(json -> gson.fromJson(json, TrabajadorResponse.class))
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

    @FXML
    private void onAddEmployeeBtn() {
        addEmployee();
    }

    @FXML
    private void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
