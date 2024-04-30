package edu.tasklynx.tasklynxjavafx.model.responses;

public class BaseResponse {
    private boolean error;
    private String errorMessage;

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
