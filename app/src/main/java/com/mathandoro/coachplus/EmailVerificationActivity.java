package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationActivity extends AppCompatActivity implements Callback<ApiResponse<Object>> {

    private Call<ApiResponse<Object>> verifyEmailCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
        Log.d("coach", t.toString());
        // todo show API failures in UI (e.g. email already used)
    }
}
