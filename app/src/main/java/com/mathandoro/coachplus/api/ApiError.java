package com.mathandoro.coachplus.api;

public class ApiError {
    protected String message;
    protected boolean success;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public ApiError(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
