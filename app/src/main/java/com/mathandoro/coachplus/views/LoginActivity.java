package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.LoginResponse;
import com.mathandoro.coachplus.models.LoginUser;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Callback<ApiResponse<LoginResponse>> {

    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    Settings settings;
    Call<ApiResponse<LoginResponse>> loginResponseCall;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        this.settings = new Settings(this);

        if(settings.isEmailVerificationRequired()){
            this.navigateToEmailVerificationActivity();
        }
        else if(settings.getToken() != null){
            this.navigateToMainActivity();
        }

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
    }


    private void navigateToEmailVerificationActivity(){
        Intent intent = new Intent(this, EmailVerificationActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity(){
        Intent intent = new Intent(this, TeamViewActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToRegistration(View view){
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    public void navigateToPasswordReset(View view){
        Intent intent = new Intent(this, PasswordResetActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        this.email = emailEditText.getText().toString();
        this.password = passwordEditText.getText().toString();
        LoginUser loginUser = new LoginUser(this.email, this.password);
        this.loginResponseCall = ApiClient.instance().userService.loginUser(loginUser);
        this.loginResponseCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
        if(call == this.loginResponseCall && response.code() == 200 && response.body().success){
            this.settings.startSession(response.body().content.token, response.body().content.user, false);
            this.navigateToMainActivity();
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {

    }
}
