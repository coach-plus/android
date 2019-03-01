package com.mathandoro.coachplus.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;

import java.lang.reflect.Member;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinTeamActivity extends AppCompatActivity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_team_activity);

        settings = new Settings(this);

        Intent intent = getIntent();
        String action = intent.getAction();

        if(action == null){
            finish();
            return;
        }

        List<String> pathSegments = intent.getData().getPathSegments();
        String tokenOrTeamId = pathSegments.get(pathSegments.size()-1);

        if(pathSegments.size() < 5) {
            return;
        }

        String teamType = pathSegments.get(2);
        if(settings.getToken() == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("tokenOrTeamId", tokenOrTeamId);
            loginIntent.putExtra("teamType", teamType);
            return;
        }

        if(teamType.equals("private")) {
            this.joinPrivateTeam(tokenOrTeamId);
        }
        else{
            this.joinPublicTeam(tokenOrTeamId);
        }

    }

    void joinPrivateTeam(String token) {
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPrivateTeam(settings.getToken(), token);
    joinTeam(apiResponseCall);
    }

    void joinPublicTeam(String teamId){
        Call<ApiResponse<Membership>> apiResponseCall = ApiClient.instance().teamService.joinPublicTeam(settings.getToken(), teamId);
        joinTeam(apiResponseCall);
    }

    void joinTeam(Call<ApiResponse<Membership>> apiResponseCall){
        apiResponseCall.enqueue(new Callback<ApiResponse<Membership>>() {
            @Override
            public void onResponse(Call<ApiResponse<Membership>> call, Response<ApiResponse<Membership>> response) {
                if(response.code() == 201){
                    navigateToMain(response.body().content);
                }
                else if(response.code() >= 400 && response.code() < 500){
                    navigateToMain(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Membership>> call, Throwable t) {

            }
        });
    }

    void navigateToMain(Membership membership){
        Intent intent = new Intent(this, TeamViewActivity.class);
        intent.putExtra(TeamViewActivity.PARAM_MEMBERSHIP, membership);
        startActivity(intent);
        finish();
    }
}
