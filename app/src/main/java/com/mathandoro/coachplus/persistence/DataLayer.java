package com.mathandoro.coachplus.persistence;

import android.content.Context;

import com.google.gson.Gson;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Request.CreateNewsRequest;
import com.mathandoro.coachplus.api.Request.DidAttendRequest;
import com.mathandoro.coachplus.api.Request.UpdatePasswordRequest;
import com.mathandoro.coachplus.api.Request.UpdateRoleRequest;
import com.mathandoro.coachplus.api.Request.UpdateUserRequest;
import com.mathandoro.coachplus.api.Request.WillAttendRequest;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.api.Response.GetNewsResponse;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.api.Response.UpdateUserInformationResponse;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.models.Device;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.UserImageUpload;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataLayer {

    static DataLayer instance;
    protected Context context;
    protected Cache cache;
    protected Settings settings;
    final String MY_MEMBERSHIPS = "myMemberships";
    final String EVENTS = "events";
    private Gson gson;


    public DataLayer(Context context){
        this.context = context;
        this.settings = new Settings(this.context);
        this.cache = new Cache(this.context);
        this.gson = new Gson();
    }

    public static DataLayer getInstance(Context context){
        if(instance == null){
            instance = new DataLayer(context.getApplicationContext());
        }
        return instance;
    }

    public Observable<GetNewsResponse> getNews(String teamId, String eventId){
        Call<ApiResponse<GetNewsResponse>> apiCall = ApiClient.instance().teamService.getNews(settings.getToken(), teamId, eventId);
        return this.apiCall(apiCall, false);
    }

    public Observable<Object> updatePassword(UpdatePasswordRequest updatePasswordRequest){
        Call<ApiResponse<Object>> updateUserPassword = ApiClient.instance().userService.updateUserPassword(settings.getToken(), updatePasswordRequest);
        return this.apiCall(updateUserPassword, false);
    }

    public Observable<UpdateUserInformationResponse> updateUserInformation(UpdateUserRequest updateUserInformationRequest){
        Call<ApiResponse<UpdateUserInformationResponse>> updateUserInformation = ApiClient.instance().userService.updateUserInformation(settings.getToken(), updateUserInformationRequest);
        return this.apiCall(updateUserInformation, false);
    }

    public Observable<Object> createNews(String teamId, String eventId, String title, String text){
        // todo error: response contains author as userId instead of reduced user (in CreateNewsResponse)
        Call<ApiResponse<Object>> news1 = ApiClient.instance().teamService.createNews(settings.getToken(), teamId, eventId, new CreateNewsRequest(title, text));
        return this.apiCall(news1, false);
    }

    public Observable<Object> deleteNews(String teamId, String eventId, String newsId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.deleteNews(settings.getToken(), teamId, eventId, newsId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<ParticipationResponse> getParticipationOfEvent(boolean useCache, String teamId, String eventId){
        Call<ApiResponse<ParticipationResponse>> participationCall = ApiClient.instance().teamService.getParticipationOfEvent(settings.getToken(), teamId, eventId);
        return this.apiCall(participationCall, useCache);
    }

    public Observable<Participation> setWillAttend(String teamId, String eventId, String userId, boolean willAttend){
        Call<ApiResponse<Participation>> participationCall = ApiClient.instance().teamService.setWillAttend(settings.getToken(), teamId, eventId, userId, new WillAttendRequest(willAttend));
        return this.apiCall(participationCall, false);
    }

    // todo return type correct ?
    public Observable<MyUserResponse> uploadUserImage(String imageBase64){
        Call<ApiResponse<MyUserResponse>> apiResponseCall = ApiClient.instance().userService.updateUserImage(settings.getToken(), new UserImageUpload(imageBase64));
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> removeUserFromTeam(String membershipId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().membershipService.deleteMembership(settings.getToken(), membershipId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Membership> joinPrivateTeam(String invitationToken) {
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPrivateTeam(settings.getToken(), invitationToken);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Membership> joinPublicTeam(String teamId){
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPublicTeam(settings.getToken(), teamId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> leaveTeam(String teamId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.leaveTeam(settings.getToken(), teamId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> deleteTeam(String teamId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.deleteTeam(settings.getToken(), teamId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Team> updateTeam(String teamId, String teamName, boolean isPublic, String selectedImageBase64){
        Team updatedTeam = new Team(teamId, teamName, isPublic, selectedImageBase64);
        Call<ApiResponse<Team>> apiResponseCall = ApiClient.instance().teamService.updateTeam(settings.getToken(), teamId, updatedTeam);
        return this.apiCall(apiResponseCall, false);
    }

   @Deprecated
    public Observable<Object> promoteUser(String teamId, String userId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.promoteUser(settings.getToken(), teamId, userId);
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Object> updateRole( String membershipId, String role){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().membershipService.setRole(settings.getToken(), membershipId, new UpdateRoleRequest((role)));
        return this.apiCall(apiResponseCall, false);
    }

    public Observable<Participation> setDidAttend(String teamId, String eventId, String userId, boolean didAttend){
        Call<ApiResponse<Participation>> participationCall = ApiClient.instance().teamService.setDidAttend(settings.getToken(), teamId, eventId, userId, new DidAttendRequest(didAttend));
        return this.apiCall(participationCall, false);
    }

    public Observable<MyMembershipsResponse> getMyMembershipsV2(boolean useCache){
        Call<ApiResponse<MyMembershipsResponse>> myUserCall = ApiClient.instance().membershipService.getMyMemberships(settings.getToken()); //.userService.getMyUser(settings.getToken());
        return this.apiCall(myUserCall, useCache);
    }

    public Observable<TeamMembersResponse> getTeamMembersV2(boolean useCache, Team team){
        Call<ApiResponse<TeamMembersResponse>> teamMembersCall = ApiClient.instance().teamService.getTeamMembers(settings.getToken(), team.get_id());
        return this.apiCall(teamMembersCall, useCache);
    }

    public void getMembershipsOfUser(String userId, final DataLayerCallback<List<Membership>> callback){
        ApiClient.instance().userService.getMembershipsOfUser(settings.getToken(), userId)
                .enqueue(new Callback<ApiResponse<MyMembershipsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyMembershipsResponse>> call,
                                   Response<ApiResponse<MyMembershipsResponse>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        List<Membership> memberships = response.body().content.getMemberships();
                        callback.dataChanged(memberships);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyMembershipsResponse>> call, Throwable t) {
                callback.error();
            }
        });
    }

    public Observable<Object> registerOrUpdateDevice(String deviceId, String pushId){
        Device device = new Device(pushId, "android", deviceId);
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().userService.registerOrUpdateDevice(settings.getToken(), settings.getUser().get_id(), device);
        return apiCall(apiResponseCall, false);
    }

    public Observable<MyUserResponse> getMyUserV2(boolean useCache){
        Call<ApiResponse<MyUserResponse>> myUserCall = ApiClient.instance().userService.getMyUser(settings.getToken());
        return this.apiCall(myUserCall, useCache);
    }

    public Observable<TeamMembersResponse> getTeamMembersV2(Team team, boolean useCache){
        String token = settings.getToken();
        Call<ApiResponse<TeamMembersResponse>> teamMembersCall = ApiClient.instance()
                .teamService.getTeamMembers(token, team.get_id());
        return this.apiCall(teamMembersCall, useCache);
    }

    public Observable<EventsResponse> getEvents(final Team team, boolean useCache) {
        Call<ApiResponse<EventsResponse>> eventsOfTeam = ApiClient.instance().teamService.getEventsOfTeam(settings.getToken(), team.get_id());
        return this.apiCall(eventsOfTeam, useCache);
    }

    public Observable<Object> deleteEvent(String teamId, String eventId){
        Call<ApiResponse<Object>> apiCall = ApiClient.instance().teamService.deleteEvent(settings.getToken(), teamId, eventId);
        return this.apiCall(apiCall, false);
    }

    public Observable<CreateEventResponse> createEvent(String teamId, Event event){
        Call<ApiResponse<CreateEventResponse>> apiCall = ApiClient.instance().teamService.createEvent(settings.getToken(), teamId, event);
        return this.apiCall(apiCall, false);
    }

    public Observable<CreateEventResponse> updateEvent(String teamId, Event event){
        Call<ApiResponse<CreateEventResponse>> apiCall = ApiClient.instance().teamService.updateEvent(settings.getToken(), teamId, event.get_id(), event);
        return this.apiCall(apiCall, false);
    }

    public Observable<Membership> registerTeam(String teamName, boolean isPublic, String teamImageBase64){
        RegisterTeam team = new RegisterTeam(teamName, isPublic, teamImageBase64);
        String token = settings.getToken();
        Call<ApiResponse<Membership>> registerTeamCall = ApiClient.instance().teamService.registerTeam(token, team);
        return this.apiCall(registerTeamCall, false);
    }

    private <T> Observable<T> apiCall(Call<ApiResponse<T>> t, boolean useCache){
        Observable<T> resultObservable = Observable.create(emitter -> {
            t.enqueue(new Callback<ApiResponse<T>>() {
                @Override
                public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                    if(response.code() >= 200 && response.code() < 300){
                    /*
                    todo cache ?
                    String serializedResponse = DataLayer.this.gson.toJson(response.body().content);
                    try {
                        cache.saveList(members, TEAM_MEMBERS, CacheContext.TEAM(finalTeam));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                        emitter.onNext(response.body().content);
                    }
                    else {
                        emitter.onError(new Throwable("API returned code " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                    emitter.onError(t);
                }
            });
        });

        return resultObservable;
    }


/*    public void createEvent(Team team, Event event, final DataLayerCallback<Event> callback){
        final String CREATE_EVENT = "createEvent";
        ApiClient.instance().teamService.createEvent(settings.getToken(), team.get_id(), event)
                .enqueue(new Callback<ApiResponse<CreateEventResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CreateEventResponse>> call, Response<ApiResponse<CreateEventResponse>> response) {
                if(response.code() == 201){
                    if(callback != null){
                        callback.dataChanged(response.body().content.getEvent());
                    }
                }
                else{
                    if(callback != null){
                        callback.error();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CreateEventResponse>> call, Throwable t) {
                callback.error();
            }
        });
    }*/


}
