package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.LoginResponse;
import com.mathandoro.coachplus.models.LoginUser;
import com.mathandoro.coachplus.models.RegisterUser;
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

    Button registrationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        this.settings = new Settings(this);

        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);

        registrationButton = findViewById(R.id.login_activity_register_button);
        registrationButton.setOnClickListener((view -> navigateToRegistration()));
    }

    private void navigateToRegistration(){
        Intent intent = new Intent(this, UserRegistrationActivity.class);
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

            this.settings.setPassword(this.password);
            this.settings.setToken(response.body().content.token);
            this.settings.setUser(response.body().content.user);

            this.navigateToMainActivity();
        }
    }

    protected void navigateToMainActivity(){
        Intent intent = new Intent(this, TeamViewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {

    }
}
