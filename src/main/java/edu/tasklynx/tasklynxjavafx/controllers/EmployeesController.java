package edu.tasklynx.tasklynxjavafx.controllers;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorResponse;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class EmployeesController implements Initializable {
    
    private List<Trabajador> list;
    private static Gson gson;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gson = new Gson();
    }

    private void loadEmployees() {
        String url = ServiceUtils.SERVER + "/employees";
        ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajoListResponse.class))
                .thenAccept(response -> {
                    if (!response.isError()) {
                        Platform.runLater(() -> tbvEmployees.getItems().setAll(response.getJobs()));
                    } else {
                        System.out.println("ERROR OBTENIENDO LISTA 1: " + response.getErrorMessage());
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO LISTA 2: " + ex.getMessage());
                    return null;
                });
    }

    private static CompletableFuture<Trabajador> getEmployeeById(String id) {
        String url = ServiceUtils.SERVER + "/employees/" + id;
        return ServiceUtils.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, TrabajadorResponse.class))
                .thenApply(response -> {
                    if (response != null && !response.isError()) {
                        return response.getEmployee();
                    } else {
                        System.out.println("ERROR OBTENIENDO EMPLEADO 1: " + response.getErrorMessage());
                        return null;
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("ERROR OBTENIENDO EMPLEADO 2: " + ex.getMessage());
                    return null;
                });
    }}
