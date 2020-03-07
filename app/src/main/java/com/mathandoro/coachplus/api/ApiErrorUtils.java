package com.mathandoro.coachplus.api;

import org.json.JSONObject;
import retrofit2.Response;

public class ApiErrorUtils {
    public static ApiError parseErrorResponse(Response<?> response){
        try {
            JSONObject jsonResponse = new JSONObject(response.errorBody().string());
            String message = jsonResponse.getString("message");
            boolean success = jsonResponse.getString("success").equals("false");
            return new ApiError(message, success);
        } catch (Exception e) {
            return new ApiError("R.string.Error", false);
        }
    }
}
