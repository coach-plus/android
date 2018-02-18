package com.mathandoro.coachplus.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.views.MainActivity.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinTeamActivity extends AppCompatActivity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

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
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.joinPrivateTeam(settings.getToken(), token);
        apiResponseCall.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if(response.code() == 201){
                    Log.e("debug", "respoonse code: " + response.code());
                    navigateToMain();
                }
                else if(response.code() >= 400 && response.code() < 500){
                    Log.e("debug", "respoonse message: " + response.body());
                    navigateToMain();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {

            }
        });
    }

    void joinPublicTeam(String teamId){
        Call<ApiResponse<Object>> apiResponseCall = ApiClient.instance().teamService.joinPublicTeam(settings.getToken(), teamId);
        apiResponseCall.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if(response.code() == 201){
                    navigateToMain();
                }
                else if(response.code() >= 400 && response.code() < 500){
                    navigateToMain();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {

            }
        });
    }

    void navigateToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
