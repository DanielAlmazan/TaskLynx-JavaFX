package edu.tasklynx.tasklynxjavafx.model.responses;

import java.util.List;

public class BaseResponse {
    protected boolean error;
    protected String errorMessage;
    protected List<String> errorsList;

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getErrorsList() { return errorsList; }
}
