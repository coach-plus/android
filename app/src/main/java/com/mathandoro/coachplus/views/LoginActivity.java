package com.mathandoro.coachplus.views;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.LoginResponse;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
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

    public static final int INTENT_RESET_PASSWORD = 1;

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
        startActivityForResult(intent, INTENT_RESET_PASSWORD);
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
        else if(response.code() == 400) {
            // showError(R.string.login_failed_wrong_input);
            showError(R.string.Error);
        }
        else {
            // showError(R.string.api_call_failed_server_issues);
            showError(R.string.Error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_RESET_PASSWORD && resultCode == RESULT_OK){
            SnackbarHelper.showText(emailEditText, R.string.Successfully_requested_a_new_password);
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
        // TODO
        // showError(R.string.api_call_failed_network_issues);
        showError(R.string.Error);
    }

    private void showError(@StringRes() int error){
        SnackbarHelper.showText(emailEditText, error);
    }

}
