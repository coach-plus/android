package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.models.Response.ApiResponse;
import com.mathandoro.coachplus.models.Team;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterTeamActivity extends AppCompatActivity {

    protected Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        settings = new Settings(this);
        Button registerTeamButton = (Button) findViewById(R.id.createTeamButton);
        final EditText teamNameEditText = (EditText) findViewById(R.id.teamNameEditText);
        final CheckBox registerTeamPublicToggleButton = (CheckBox)findViewById(R.id.registerTeamPublicToggleButton);

        registerTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeam(teamNameEditText.getText().toString(), registerTeamPublicToggleButton.isActivated());
            }
        });
    }

    void registerTeam(String teamName, boolean isPublic){
        RegisterTeam team = new RegisterTeam(teamName, isPublic);
        String token = settings.getToken();
        Call<ApiResponse<Team>> registerTeamCall = ApiClient.instance().teamService.registerTeam(token, team);
        registerTeamCall.enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if(response.code() == 201){
                    success();
                }
                else{
                    fail();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                fail();
            }
        });
    }

    void success(){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    void fail(){
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
