package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.models.ApiResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.MyMembershipsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by dominik on 31.03.17.
 */

public interface MembershipService {
    @GET("memberships/my")
    Call<ApiResponse<MyMembershipsResponse>> getMyMemberships(@Header("X-ACCESS-TOKEN") String contentRange);
}
