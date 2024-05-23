package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.tasklynx.tasklynxjavafx.TaskLynxController;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import edu.tasklynx.tasklynxjavafx.model.responses.BaseResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.LocalDateAdapter;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import edu.tasklynx.tasklynxjavafx.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class EmployeesController implements Initializable {
    @FXML
    private VBox panelPendingTasks;
    @FXML
    private Label lblPendingTasks;
    @FXML
    private TableView<Trabajo> tbvPendingTasks;
    @FXML
    private TableView<Trabajador> tbvEmployees;
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

    private List<Trabajador> list;
    private Gson gson;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

        detailContainer.getChildren().remove(blockDetail);

        addImages();

        loadEmployees();
    }

    @FXML
    private void addImages() {
        URL linkAdd = getClass().getResource("/icons/btn-add.png");

        Image iconAdd = new Image(linkAdd.toString(), 24, 24, false, true);

        btnAdd.setGraphic(new ImageView(iconAdd));
    }

    @FXML
    public void onSelectedRow(MouseEvent mouseEvent) {
        toggleDetailView();
    }

    @FXML
    public void onKeyReleased(KeyEvent keyEvent) {
        toggleDetailView();
    }

    @FXML
    public void onAddEmployee(ActionEvent actionEvent) {
        modalAddEmployee(actionEvent);
    }

    @FXML
    public void deleteEmployee(ActionEvent actionEvent) {
        Alert alert = Utils.showAlert(
                Alert.AlertType.CONFIRMATION,
                "Caution",
                "Are you sure to delete this employee?",
                "This action can't be undone."
        );

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Trabajador trabajador = tbvEmployees.getSelectionModel().getSelectedItem();

            if(trabajador != null) {
                String url = ServiceUtils.SERVER + "/trabajadores/" + trabajador.getIdTrabajador();
                ServiceUtils.getResponseAsync(url, null, "DELETE")
                        .thenApply(json -> gson.fromJson(json, BaseResponse.class))
                        .thenAccept(response -> Platform.runLater(() -> {
                            if (!response.isError()) {
                                loadEmployees();
                                Utils.showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Success",
                                        "Employee deleted sucessfully",
                                        "The employee hass been removed from the application"
                                ).show();
                            } else {
                                Utils.showAlert(
                                        Alert.AlertType.ERROR,
                                        "Error",
                                        "Error deleting the employee",
                                        "The employee has pending tasks"
                                ).show();
                            }
                        }))
                        .exceptionally(ex -> {
                            System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                            return null;
                        });
            }
        }
    }

    private void loadEmployees() {
        tbvEmployees.getSelectionModel().clearSelection();
        String url = ServiceUtils.SERVER + "/trabajadores";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> tbvEmployees.getItems().setAll(response.getEmployees()));
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });

        toggleDetailView();
    }

    private void toggleDetailView() {
        Trabajador trabajador = tbvEmployees.getSelectionModel().getSelectedItem();

        if (trabajador != null) {
            if (!detailContainer.getChildren().contains(blockDetail)) {
                detailContainer.setAlignment(Pos.TOP_CENTER);
                detailContainer.getChildren().add(blockDetail);
            }

            lblDetail.setText(trabajador.getNombre() + " " + trabajador.getApellidos() + " - Detail");
            lblName.setText(trabajador.getNombre());
            lblSurname.setText(trabajador.getApellidos());
            lblDni.setText(trabajador.getDni());
            lblSpeciality.setText(trabajador.getEspecialidad());
            lblEmail.setText(trabajador.getEmail());
        } else {
            lblDetail.setText("Select an employee to see his details");
            detailContainer.setAlignment(Pos.CENTER);
            detailContainer.getChildren().remove(blockDetail);
        }

        toggleAssigments();
    }

    private void toggleAssigments() {
        Trabajador trabajador = tbvEmployees.getSelectionModel().getSelectedItem();

        if (trabajador != null) {
            getEmployeePendingTasks(trabajador.getIdTrabajador());
        } else {
            panelPendingTasks.setAlignment(Pos.CENTER);
            panelPendingTasks.getChildren().remove(tbvPendingTasks);
            lblPendingTasks.setText("Select an employee to see his pending tasks");
        }
    }

    private void getEmployeePendingTasks(String id) {
        String url = ServiceUtils.SERVER + "/trabajadores/" + id + "/trabajos/pendientes";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> {
                            if(!response.getJobs().isEmpty()) {
                                lblPendingTasks.setText("Pending Tasks");
                                panelPendingTasks.setAlignment(Pos.TOP_CENTER);

                                if(!panelPendingTasks.getChildren().contains(tbvPendingTasks)) {
                                    panelPendingTasks.getChildren().add(tbvPendingTasks);
                                }

                                tbvPendingTasks.getItems().setAll(response.getJobs());
                            } else {
                                lblPendingTasks.setText("This employee doesn't any have pending task");
                                panelPendingTasks.setAlignment(Pos.CENTER);
                                panelPendingTasks.getChildren().remove(tbvPendingTasks);
                            }
                        });
                    } else {
                        System.out.println("ERROR OBTENIENDO TRABAJOS 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO TRABAJOS 2: " + ex.getMessage());
                    return null;
                });
    }

    private void modalAddEmployee(ActionEvent actionEvent) {
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/edu/tasklynx/tasklynxjavafx/modals/newEmployeeModal.fxml")));
        Utils.showModal(view, (Stage) tbvEmployees.getScene().getWindow()).showAndWait();
        loadEmployees();
    }
}


