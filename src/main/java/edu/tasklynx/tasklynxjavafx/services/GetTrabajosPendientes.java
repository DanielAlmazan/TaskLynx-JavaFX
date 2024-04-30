package edu.tasklynx.tasklynxjavafx.services;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajoListResponse;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetTrabajosPendientes extends Service<TrabajoListResponse> {

    String filter;

    public GetTrabajosPendientes() {
        filter = "pending";
    }

    @Override
    protected Task<TrabajoListResponse> createTask() {
        return new Task<TrabajoListResponse>() {
            @Override
            protected TrabajoListResponse call() throws Exception {
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/tasks/" + filter, null, "GET");
                Gson gson = new Gson();
                TrabajoListResponse response = gson.fromJson(json, TrabajoListResponse.class);
                return response;
            }
        };
    }
}