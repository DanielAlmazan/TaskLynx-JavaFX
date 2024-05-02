package edu.tasklynx.tasklynxjavafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TasksController implements Initializable {
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Button btnAssignEmployee;
    @FXML
    private Label lblDetail;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblStartingDate;
    @FXML
    private Label lblResponsible;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blockDetail.setVisible(false);
    }

    @FXML
    public void prueba(ActionEvent actionEvent) {
        detailContainer.setAlignment(Pos.TOP_CENTER);
        blockDetail.setVisible(true);
        lblDetail.setText("Limpieza - Detalle");
    }
}
