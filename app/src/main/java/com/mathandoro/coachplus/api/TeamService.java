package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.api.Request.CreateNewsRequest;
import com.mathandoro.coachplus.api.Request.DidAttendRequest;
import com.mathandoro.coachplus.api.Request.WillAttendRequest;
import com.mathandoro.coachplus.api.Response.CreateNewsResponse;
import com.mathandoro.coachplus.api.Response.GetNewsResponse;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.News;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.InvitationResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.Team;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by d059458 on 03.04.17.
 */

public interface TeamService {

    @POST("teams/register")
    Call<ApiResponse<Membership>> registerTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Body RegisterTeam team);

    @GET("teams/{teamId}/members")
    Call<ApiResponse<TeamMembersResponse>> getTeamMembers(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @DELETE("teams/{teamId}")
    Call<ApiResponse<Object>> deleteTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @PUT("teams/{teamId}")
    Call<ApiResponse<Team>> updateTeam(@Header("X-ACCESS-TOKEN") String token, @Path("teamId") String teamId, @Body() Team team);

    @GET("teams/{teamId}/events")
    Call<ApiResponse<EventsResponse>> getEventsOfTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @POST("teams/{teamId}/events")
    Call<ApiResponse<CreateEventResponse>> createEvent(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Body Event event);

    @PUT("teams/{teamId}/events/{eventId}")
    Call<ApiResponse<CreateEventResponse>> updateEvent(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId, @Body Event event);

    @DELETE("teams/{teamId}/events/{eventId}")
    Call<ApiResponse<Object>> deleteEvent(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId);

    @GET("teams/{teamId}/events/{eventId}/participation")
    Call<ApiResponse<ParticipationResponse>> getParticipationOfEvent(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId);

    @PUT("teams/{teamId}/events/{eventId}/participation/{userId}/didAttend")
    Call<ApiResponse<Participation>> setDidAttend(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId, @Path("userId") String userId, @Body DidAttendRequest didAttend);

    @PUT("teams/{teamId}/events/{eventId}/participation/{userId}/willAttend")
    Call<ApiResponse<Participation>> setWillAttend(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId, @Path("userId") String userId, @Body WillAttendRequest willAttend);

    @POST("teams/private/join/{token}")
    Call<ApiResponse<Membership>> joinPrivateTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("token") String token);

    @POST("teams/public/join/{teamId}")
    Call<ApiResponse<Membership>> joinPublicTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @POST("teams/{teamId}/invite")
    Call<ApiResponse<InvitationResponse>> createInvitationUrl(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @GET("teams/{teamId}/events/{eventId}/news")
    Call<ApiResponse<GetNewsResponse>> getNews(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId);

    @POST("teams/{teamId}/events/{eventId}/news")
    Call<ApiResponse<Object>> createNews(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId, @Body CreateNewsRequest news);

    @DELETE("teams/{teamId}/events/{eventId}/news/{newsId}")
    Call<ApiResponse<Object>> deleteNews(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("eventId") String eventId, @Path("newsId") String newsId);

    @DELETE("teams/{teamId}/memberships")
    Call<ApiResponse<Object>> leaveTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @PUT("teams/{teamId}/coaches/{userId}")
    Call<ApiResponse<Object>> promoteUser(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Path("userId") String userId);
}


