package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.ApiResponse;
import com.mathandoro.coachplus.models.LoginResponse;
import com.mathandoro.coachplus.models.LoginUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Callback<ApiResponse<LoginResponse>> {

    EditText emailEditText;
    EditText passwordEditText;
    Settings settings;
    Call<ApiResponse<LoginResponse>> loginResponseCall;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.settings = new Settings(this);

        emailEditText = (EditText)findViewById(R.id.loginEmailEditText);
        passwordEditText = (EditText)findViewById(R.id.loginPasswordEditText);
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
            this.settings.setEmail(this.email);
            this.settings.setToken(response.body().content.token);
            this.navigateToMainActivity();
        }
    }

    protected void navigateToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {

    }
}
