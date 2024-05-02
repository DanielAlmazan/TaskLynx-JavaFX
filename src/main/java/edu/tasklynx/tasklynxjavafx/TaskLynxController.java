package edu.tasklynx.tasklynxjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TaskLynxController implements Initializable {
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
        } catch (IOException e) {
            System.out.println("Error: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}