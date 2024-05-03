package edu.tasklynx.tasklynxjavafx.model.responses;

public class BaseResponse {
    protected boolean error;
    protected String errorMessage;

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
