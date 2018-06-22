package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.LoginResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.models.LoginUser;
import com.mathandoro.coachplus.models.RegisterUser;
import com.mathandoro.coachplus.api.Response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @GET("users/me")
    Call<ApiResponse<MyUserResponse>> getMyUser(@Header("X-ACCESS-TOKEN") String accessToken);

    @POST("users/verification/{token}")
    Call<ApiResponse<Object>> verifyEmail(@Path("token") String token);

    @GET("users/{userId}/memberships")
    Call<ApiResponse<MyMembershipsResponse>> getMembershipsOfUser(@Header("X-ACCESS-TOKEN") String accessToken, @Path("userId") String userId);
}
