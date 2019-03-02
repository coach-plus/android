package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.api.Request.UpdateRoleRequest;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by dominik on 31.03.17.
 */

public interface MembershipService {
    @GET("memberships/my")
    Call<ApiResponse<MyMembershipsResponse>> getMyMemberships(@Header("X-ACCESS-TOKEN") String accessToken);

    @PUT("memberships/{membershipId}/role")
    Call<ApiResponse<Object>> setRole(@Header("X-ACCESS-TOKEN") String accessToken, @Path("membershipId") String membershipId, @Body UpdateRoleRequest body);
}
