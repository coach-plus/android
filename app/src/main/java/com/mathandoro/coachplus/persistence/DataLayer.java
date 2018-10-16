package com.mathandoro.coachplus.persistence;

import android.content.Context;

import com.google.gson.Gson;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Request.CreateNewsRequest;
import com.mathandoro.coachplus.api.Request.DidAttendRequest;
import com.mathandoro.coachplus.api.Request.WillAttendRequest;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.api.Response.GetNewsResponse;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;

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

    private <T> Observable<T> apiCall(Call<ApiResponse<T>> t, boolean useCache){
        Observable<T> resultObservable = Observable.create(emitter -> {
            t.enqueue(new Callback<ApiResponse<T>>() {
                @Override
                public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                    if(response.code() == 200 || response.code() == 201){
                    /*
                    todo cache
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


    public void createEvent(Team team, Event event, final DataLayerCallback<Event> callback){
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
    }

}
