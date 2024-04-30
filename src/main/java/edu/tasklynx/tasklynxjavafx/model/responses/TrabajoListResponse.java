package edu.tasklynx.tasklynxjavafx.model.responses;

import edu.tasklynx.tasklynxjavafx.model.Trabajo;

import java.util.List;

public class TrabajoListResponse extends BaseResponse {
    List<Trabajo> result;
    
    public List<Trabajo> getJobs() {
        return result;
    }
}
