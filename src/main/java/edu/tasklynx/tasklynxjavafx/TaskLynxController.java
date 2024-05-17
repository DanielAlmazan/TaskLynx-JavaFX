package edu.tasklynx.tasklynxjavafx;

import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class TaskLynxController implements Initializable {
    @FXML
    private Button btnShowTasks;
    @FXML
    private Button btnShowEmployees;
    @FXML
    private Button btnShowReports;
    @FXML
    private Pane panel;

    public static boolean pendingChanges;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pendingChanges = false;
        showTasks();
    }

    @FXML
    protected void showTasks() {
        changePanel("tasks");
    }

    @FXML
    protected void showEmployees() {
        changePanel("employees");
    }

    @FXML
    protected void showReports() {
        changePanel("reports");
    }

    private void changePanel(String view) {
        if(!pendingChanges) {
            try {
                Pane secondPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view + ".fxml")));
                panel.getChildren().setAll(secondPanel);
                changeActiveTab(view);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else  {
            Alert alert = Utils.showAlert(
                    Alert.AlertType.CONFIRMATION,
                    "Caution",
                    "You have pending changes to confirm",
                    "Are you sure to exit without committing these changes? You will lose those pending changes"
            );

            Optional<ButtonType> result = alert.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK) {
                pendingChanges = false;
                changePanel(view);
            }
        }
    }

    private void changeActiveTab(String tab) {
        btnShowTasks.getStyleClass().remove("active");
        btnShowEmployees.getStyleClass().remove("active");
        btnShowReports.getStyleClass().remove("active");
        switch (tab) {
            case "tasks":
                btnShowTasks.getStyleClass().add("active");
                break;
            case "employees":
                btnShowEmployees.getStyleClass().add("active");
                break;
            case "reports":
                btnShowReports.getStyleClass().add("active");
                break;
        }
    }
}