package edu.tasklynx.tasklynxjavafx.controllers.modalsControllers;

import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewTaskModalController implements Initializable {
    public TextField tiCodTrabajo;
    public TextField tiCategoria;
    public TextField tiDescripcion;
    public DatePicker dpFecIni;
    public DatePicker dpFecFin;
    public ChoiceBox<Trabajador> cbIdTrabajador;
    public ChoiceBox<Integer> cbPrioridad;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbPrioridad.getItems().addAll(1, 2, 3, 4);
    }

    private void loadWorkers() {
        
    }
    
    

}
