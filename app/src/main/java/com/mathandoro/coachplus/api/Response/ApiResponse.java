package com.mathandoro.coachplus.api.Response;


/**
 * Created by dominik on 26.03.17.
 */

public class ApiResponse<T> {
    public String message;
    public boolean success;
    public T content;

    public ApiResponse(String message, boolean success, T content) {
        this.message = message;
        this.success = success;
        this.content = content;
    }
}
