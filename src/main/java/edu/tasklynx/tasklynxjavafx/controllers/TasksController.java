package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.services.GetTrabajosPendientes;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TasksController implements Initializable {
    @FXML
    private ListView<Trabajo> lstTasks;
    @FXML
    private VBox detailContainer;
    @FXML
    private VBox blockDetail;
    @FXML
    private Button btnAdd;
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

    private List<Trabajo> list;
    private Gson gson;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        detailContainer.getChildren().remove(blockDetail);
        addImages();
        gson = new Gson();

        list = new ArrayList<>();

        lstTasks.setItems(FXCollections.observableList(list));
    }

    @FXML
    public void prueba(ActionEvent actionEvent) {
        detailContainer.setAlignment(Pos.TOP_CENTER);
        detailContainer.getChildren().add(blockDetail);
        lblDetail.setText("Limpieza - Detalle");

        String url = ServiceUtils.SERVER + "/tasks/pending";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> {
                    System.out.println(json);
                    return gson.fromJson(json, TrabajoListResponse.class);
                })
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> lstTasks.getItems().setAll(response.getJobs()));
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
    }

    private void addImages() {
        URL linkAdd = getClass().getResource("/icons/btn-add.png");

        Image iconAdd = new Image(linkAdd.toString(), 24, 24, false, true);

        btnAdd.setGraphic(new ImageView(iconAdd));
    }
}
