package com.mathandoro.coachplus.persistence;

import android.content.Context;

import com.google.gson.Gson;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.User;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;


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

    public void getMyMemberships(final DataLayerCallback<List<Membership>> callback){
        if(this.cache.exists(MY_MEMBERSHIPS, CacheContext.DEFAULT())){
            try {
                List<Membership> membershipList = cache.readList(MY_MEMBERSHIPS, CacheContext.DEFAULT(), Membership.class);
                if(callback != null){
                    callback.dataChanged(membershipList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ApiClient.instance().membershipService.getMyMemberships(settings.getToken()).enqueue(new Callback<ApiResponse<MyMembershipsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyMembershipsResponse>> call, Response<ApiResponse<MyMembershipsResponse>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        List<Membership> memberships = response.body().content.getMemberships();
                        callback.dataChanged(memberships);
                        try {
                            cache.saveList(memberships, MY_MEMBERSHIPS, CacheContext.DEFAULT());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyMembershipsResponse>> call, Throwable t) {
                callback.error();
            }
        });
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

//    public void getMyUser(){
//        // todo cache?
//        ApiClient.instance().userService.getMyUser(settings.getToken())
//                .enqueue(new Callback<ApiResponse<MyUserResponse>>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse<MyUserResponse>> call,
//                                           Response<ApiResponse<MyUserResponse>> response) {
//                        if(response.code() == 200){
//                                JWTUser user = response.body().content.user;
//                                AppState.instance().myUser.publish(user);
//                            }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<MyUserResponse>> call, Throwable t) {
//                        // todo callback with error?
//                    }
//                });
//    }

    // todo
    public void getMyUser(boolean useCache, final DataLayerSuccessCallback<MyUserResponse> callback){
        Call<ApiResponse<MyUserResponse>> myUserCall = ApiClient.instance().userService.getMyUser(settings.getToken());
        this.getData(myUserCall, useCache, callback);
    }

    private <T> void getData(Call<ApiResponse<T>> t,  boolean useCache, final DataLayerSuccessCallback<T> callback){
        t.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        /*
                        String serializedResponse = DataLayer.this.gson.toJson(response.body().content);
                        try {
                            cache.saveList(members, TEAM_MEMBERS, CacheContext.TEAM(finalTeam));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        callback.dataChanged(response.body().content);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {

            }
        });
    }

    public void getTeamMembers(Team team, boolean useCache, final DataLayerCallback<List<TeamMember>> callback){
        final String TEAM_MEMBERS = "teamMembers";
        final Team finalTeam = team;

        if(this.cache.exists(TEAM_MEMBERS, CacheContext.TEAM(team)) && useCache){
            try {
                List<TeamMember> teamMembers = cache.readList(TEAM_MEMBERS, CacheContext.TEAM(team), TeamMember.class);
                if(callback != null){
                    callback.dataChanged(teamMembers);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ApiClient.instance().teamService.getTeamMembers(settings.getToken(), team.get_id()).enqueue(new Callback<ApiResponse<TeamMembersResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeamMembersResponse>> call, Response<ApiResponse<TeamMembersResponse>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        List<TeamMember> members = response.body().content.getMembers();
                        callback.dataChanged(members);
                        try {
                            cache.saveList(members, TEAM_MEMBERS, CacheContext.TEAM(finalTeam));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeamMembersResponse>> call, Throwable t) {
                callback.error();
            }
        });
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

    public void getEvents(final Team team, boolean useCache, final DataLayerCallback<List<Event>> callback){
        if(this.cache.exists(EVENTS, CacheContext.TEAM(team)) && useCache){
            try {
                List<Event> events = cache.readList(EVENTS, CacheContext.TEAM(team), Event.class);
                if(callback != null){
                    callback.dataChanged(events);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ApiClient.instance().teamService.getEventsOfTeam(settings.getToken(), team.get_id()).enqueue(new Callback<ApiResponse<EventsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<EventsResponse>> call, Response<ApiResponse<EventsResponse>> response) {
                if(response.code() == 200){
                    if(callback != null){
                        List<Event> events = response.body().content.getEvents();
                        callback.dataChanged(events);
                        try {
                            cache.saveList(events, EVENTS, CacheContext.TEAM(team));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if(callback != null){
                        callback.error();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<EventsResponse>> call, Throwable t) {
                callback.error();
            }
        });

    }


}
