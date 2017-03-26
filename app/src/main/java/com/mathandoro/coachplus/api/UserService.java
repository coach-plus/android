package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.models.RegisterUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by dominik on 24.03.17.
 */

public interface UserService {

    @POST("users/register")
    Call<Object> registerUser(@Body RegisterUser registerUser);

    @POST("users/verification/{token}")
    Call<Object> verifyEmail(@Path("token") String token);
}


