package edu.tasklynx.tasklynxjavafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changePanel("tasks");
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
        try {
            Pane secondPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view + ".fxml")));
            panel.getChildren().setAll(secondPanel);
            changeActiveTab(view);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
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

    public static void showModal(FXMLLoader view, ActionEvent actionEvent) {
        Scene mainScene = null;

        try {
            mainScene = new Scene(view.load());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (mainScene != null) {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(mainScene);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            secondaryStage.initOwner(stage);
            secondaryStage.showAndWait();
        }
    }
}