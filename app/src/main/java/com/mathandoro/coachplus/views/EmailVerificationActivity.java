package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationActivity extends AppCompatActivity implements Callback<ApiResponse<Object>> {

    private Call<ApiResponse<Object>> verifyEmailCall;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification_activity);

        settings = new Settings(this);

        Intent intent = getIntent();
        String action = intent.getAction();

        if(action == null){
            // todo check if email is already verified
            return;
        }
        List<String> pathSegments = intent.getData().getPathSegments();
        String token = pathSegments.get(pathSegments.size()-1);

        this.verifyEmailCall = ApiClient.instance().userService.verifyEmail(token);
        this.verifyEmailCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
        if(call == this.verifyEmailCall){
            if(response.code() == 200) {
                settings.confirmEmailVerification();
                navigateToTeamView();
            }
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
        Log.d("coach", t.toString());
        // todo show API failures in UI (e.g. email already used)
    }

    public void continueToTeamView(View view) {
        navigateToTeamView();
    }

    private void navigateToTeamView(){
        Intent intent = new Intent(this, TeamViewActivity.class);
        startActivity(intent);
        finish();
    }
}
