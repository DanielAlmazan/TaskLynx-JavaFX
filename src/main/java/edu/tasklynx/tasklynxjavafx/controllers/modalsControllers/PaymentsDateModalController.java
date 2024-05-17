package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class PaymentsDateModalController {

    @FXML
    public ChoiceBox<Trabajador> cbTrabajador;
    @FXML
    public Button btnGeneratePaymentReport;
    @FXML
    public Button btnCancel;

    public void initialize() {
    }

    public void onGeneratePaymentReportBtn() {
    }

    public void onCancelBtn() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
