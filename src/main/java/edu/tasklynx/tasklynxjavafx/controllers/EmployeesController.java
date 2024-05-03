package edu.tasklynx.tasklynxjavafx.controllers;

import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class EmployeesController {
    @FXML
    private TableView<Trabajo> tbvTasks;
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Button btnAdd;
    @FXML
    private Label lblDetail;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSurname;
    @FXML
    private Label lblDni;
    @FXML
    private Label lblSpeciality;
    @FXML
    private Label lblEmail;
}
