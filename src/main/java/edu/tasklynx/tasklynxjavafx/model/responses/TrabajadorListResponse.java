package edu.tasklynx.tasklynxjavafx.model.responses;

import edu.tasklynx.tasklynxjavafx.model.Trabajador;

import java.util.List;

public class TrabajadorListResponse extends BaseResponse {
    List<Trabajador> result;
    
    public List<Trabajador> getEmployees() {
        return result;
    }
}
