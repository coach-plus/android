package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.models.Response.ApiResponse;
import com.mathandoro.coachplus.models.Response.LoginResponse;
import com.mathandoro.coachplus.models.LoginUser;
import com.mathandoro.coachplus.models.RegisterUser;
import com.mathandoro.coachplus.models.Response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by dominik on 24.03.17.
 */

public interface UserService {

    @POST("users/register")
    Call<ApiResponse<RegistrationResponse>> registerUser(@Body RegisterUser registerUser);

    @POST("users/login")
    Call<ApiResponse<LoginResponse>> loginUser(@Body LoginUser loginUser);

    @POST("users/verification/{token}")
    Call<ApiResponse<Object>> verifyEmail(@Path("token") String token);
}
