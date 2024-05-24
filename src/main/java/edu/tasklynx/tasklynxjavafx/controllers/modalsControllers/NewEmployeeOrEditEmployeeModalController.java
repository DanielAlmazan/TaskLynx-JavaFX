package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class NewEmployeeOrEditEmployeeModalController {

    @FXML
    public Label lblErrorId;
    @FXML
    public Label lblErrorDni;
    @FXML
    public Label lblErrorName;
    @FXML
    public Label lblErrorSurname;
    @FXML
    public Label lblErrorPassword;
    @FXML
    public Label lblErrorSpeciality;
    @FXML
    public Label lblErrorEmail;

    @FXML
    public TextField tiId;
    @FXML
    public TextField tiDni;
    @FXML
    public TextField tiName;
    @FXML
    public TextField tiSurname;
    @FXML
    public TextField tiSpeciality;
    @FXML
    public TextField tiEmail;

    @FXML
    public Button btnAddOrEditEmployee;
    @FXML
    public Button btnCancel;

    private Gson gson;

    private boolean isEditing;

    public void initialize(Trabajador employee) {
        if (employee != null) {
            isEditing = true;
            System.out.println("isEditing true");
            btnAddOrEditEmployee.setText("Edit Employee");
            tiId.setText(employee.getIdTrabajador());
            tiId.setDisable(true);
            tiDni.setText(employee.getDni());
            tiName.setText(employee.getNombre());
            tiSurname.setText(employee.getApellidos());
            tiSpeciality.setText(employee.getEspecialidad());
            tiEmail.setText(employee.getEmail());
        } else {
            isEditing = false;
            System.out.println("isEditing false");
            btnAddOrEditEmployee.setText("Add Employee");
        }
    }

    //region Checkings for the fields
    private String checkId(String id) {
        if (id == null || id.isEmpty()) {
            return "The ID is required.";
        } else if (id.length() > 5) {
            return "The ID must be less than 5 characters.";
        }
        return null;
    }

    private String checkDNI(String DNI) {
        if (DNI == null || DNI.isEmpty()) {
            return "The DNI is required.";
        } else if (DNI.length() > 9) {
            return "The DNI must be less than 9 characters.";
        }
        return null;
    }

    private String checkName(String name) {
        if (name.length() > 100) {
            return "The name must be less than 100 characters.";
        }
        return null;
    }

    private String checkSurname(String surname) {
        if (surname.length() > 100) {
            return "The surname must be less than 100 characters.";
        }
        return null;
    }

    private String checkSpeciality(String speciality) {
        if (speciality == null || speciality.isEmpty()) {
            return "The speciality is required.";
        } else if (speciality.length() > 50) {
            return "The speciality must be less than 50 characters.";
        }
        return null;
    }

    private String checkEmail(String email) {
        Pattern emailRegex = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        if (email.length() > 150) {
            return "The email must be less than 150 characters.";
        }

        if (!emailRegex.matcher(email).matches()) {
            return "The email is not valid.";
        }
        return null;
    }

    private boolean checkFields() {
        // Check the fields for errors
        lblErrorId.setText(checkId(tiId.getText()));
        lblErrorDni.setText(checkDNI(tiDni.getText()));
        lblErrorName.setText(checkName(tiName.getText()));
        lblErrorSurname.setText(checkSurname(tiSurname.getText()));
        lblErrorSpeciality.setText(checkSpeciality(tiSpeciality.getText()));
        lblErrorEmail.setText(checkEmail(tiEmail.getText()));

        // Show the error labels if there is an error
        lblErrorId.setVisible(lblErrorId.getText() != null);
        lblErrorDni.setVisible(lblErrorDni.getText() != null);
        lblErrorName.setVisible(lblErrorName.getText() != null);
        lblErrorSurname.setVisible(lblErrorSurname.getText() != null);
        lblErrorSpeciality.setVisible(lblErrorSpeciality.getText() != null);
        lblErrorEmail.setVisible(lblErrorEmail.getText() != null);

        // Return if there is an error
        return lblErrorId.isVisible()
                && lblErrorDni.isVisible()
                && lblErrorName.isVisible()
                && lblErrorSurname.isVisible()
                && lblErrorSpeciality.isVisible()
                && lblErrorPassword.isVisible()
                && lblErrorEmail.isVisible();
    }

    //endregion

    // Method to add a new employee
    private void addEmployee(Trabajador employee) {

        String url = ServiceUtils.SERVER + "/trabajadores";

        // Serialize the data to JSON
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        String data = gson.toJson(employee);

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

    private void editEmployee(Trabajador employee) {

        String url = ServiceUtils.SERVER + "/trabajadores/" + employee.getIdTrabajador();

        // Serialize the data to JSON
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        String data = gson.toJson(employee);

        // Call to the service to edit the employee
        ServiceUtils.getResponseAsync(url, data, "PUT")
                .thenApply(json -> gson.fromJson(json, TrabajadorResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            // Show a success alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setHeaderText("Employee edited.");
                            alert.setContentText("The employee was edited successfully.");
                            alert.showAndWait();
                            onCancelBtn();
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Show an error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error editing the employee");
                            alert.setContentText(response.getErrorMessage());
                            alert.showAndWait();
                        });
                    }
                });
    }

    // Event handlers
    public void onAddOrEditEmployeeBtn() {
        if (!checkFields()) {
            try {
                Trabajador employee = new Trabajador(
                        tiId.getText(),
                        tiDni.getText(),
                        tiName.getText(),
                        tiSurname.getText(),
                        tiDni.getText(),
                        tiEmail.getText(),
                        tiSpeciality.getText()
                );
                if (!isEditing) {
                    addEmployee(employee);
                } else {
                    editEmployee(employee);
                }
            } catch (NullPointerException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Error: There are errors in the form.");
        }
    }

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
