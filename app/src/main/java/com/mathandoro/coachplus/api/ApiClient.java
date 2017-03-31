package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.models.Membership;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dominik on 24.03.17.
 */

public class ApiClient {

    protected Retrofit retrofit;
    protected String baseUrl;
    public UserService userService;
    public MembershipService membershipService;

    private static ApiClient instance;


    private ApiClient(){
        this.baseUrl = BuildConfig.BASE_URL;
        this.retrofit = new Retrofit.Builder().baseUrl(this.baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        this.userService = retrofit.create(UserService.class);
        this.membershipService = retrofit.create(MembershipService.class);
    }

    public static ApiClient instance(){
        if(instance == null){
            instance = new ApiClient();
        }
        return instance;
    }
}
