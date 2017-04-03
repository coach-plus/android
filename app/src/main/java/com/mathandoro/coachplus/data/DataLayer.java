package com.mathandoro.coachplus.data;

import android.content.Context;
import android.util.Log;

import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.ApiResponse;
import com.mathandoro.coachplus.models.MyMembershipsResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.models.TeamMembersResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataLayer {

    static DataLayer instance;
    protected Context context;
    protected Cache cache;
    protected boolean offlineMode = false;
    protected Settings settings;



    private DataLayer(Context context){
        this.context = context;
        this.settings = new Settings(this.context);
        this.cache = new Cache(this.context);
    }

    public static DataLayer getInstance(Context context){
        if(instance == null){
            instance = new DataLayer(context.getApplicationContext());
        }
        return instance;
    }

    public void setOfflineMode(boolean offlineMode){
        this.offlineMode = offlineMode;
    }

    public void getMyMemberships(final DataLayerCallback<List<Membership>> callback){
        final String MY_MEMBERSHIPS = "myMemberships";

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
        if(this.offlineMode){
            return;
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
        if(this.offlineMode){
            return;
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
}
