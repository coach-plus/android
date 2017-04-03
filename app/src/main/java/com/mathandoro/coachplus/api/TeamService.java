package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.models.ApiResponse;
import com.mathandoro.coachplus.models.TeamMembersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by d059458 on 03.04.17.
 */

public interface TeamService {

    @GET("teams/{teamId}/members")
    Call<ApiResponse<TeamMembersResponse>> getTeamMembers(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamid);
}
