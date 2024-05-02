package edu.tasklynx.tasklynxjavafx.services;

import com.google.gson.Gson;
import edu.tasklynx.tasklynxjavafx.model.responses.TrabajadorListResponse;
import edu.tasklynx.tasklynxjavafx.utils.ServiceUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GetTrabajadores extends Service<TrabajadorListResponse> {

    String filter;

    public GetTrabajadores() {
        filter = "";
    }

    @Override
    protected Task<TrabajadorListResponse> createTask() {
        return new Task<TrabajadorListResponse>() {
            @Override
            protected TrabajadorListResponse call() throws Exception {
                String json = ServiceUtils.getResponse(
                        ServiceUtils.SERVER + "/employees" + filter, null, "GET");
                Gson gson = new Gson();
                TrabajadorListResponse response = gson.fromJson(json, TrabajadorListResponse.class);
                return response;
            }
        };
    }
}