package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.models.RegisterUser;
import com.mathandoro.coachplus.api.Response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserRegistrationActivity extends AppCompatActivity implements Callback<ApiResponse<RegistrationResponse>> {

    TextInputEditText firstnameEditText;
    TextInputEditText lastnameEditText;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    TextInputEditText passwordRepeatEditText;
    Button registrationButton;

    Settings settings;

    String firstname;
    String lastname;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        this.settings = new Settings(this);

        firstnameEditText = findViewById(R.id.user_registration_firstname_input);
        lastnameEditText = findViewById(R.id.user_registration_lastname_input);
        emailEditText = findViewById(R.id.user_registration_email_input);
        passwordEditText = findViewById(R.id.user_registration_password_input);
        passwordRepeatEditText = findViewById(R.id.user_registration_password_repeat_input);
        registrationButton = findViewById(R.id.user_registration_register_button);

        registrationButton.setOnClickListener(view -> registerUser());
    }

    public void registerUser(){
        firstname = firstnameEditText.getText().toString();
        lastname = lastnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String repeatPassword = passwordRepeatEditText.getText().toString();

        if(TextUtils.isEmpty(firstname)){
            firstnameEditText.setError("please enter your firstname");
            return;
        }

        if(TextUtils.isEmpty(lastname)){
            lastnameEditText.setError("please enter your lastname");
            return;
        }

        if(!this.isValidEmail(email)){
            emailEditText.setError("Please enter a valid email address");
            return;
        }

        if(!password.equals(repeatPassword)){
            passwordRepeatEditText.setError("passwords do not match");
            return;
        }

        Call<ApiResponse<RegistrationResponse>> registerUserCall = ApiClient.instance().userService.registerUser(new RegisterUser(firstname, lastname, email, password));
        registerUserCall.enqueue(this);
    }

    public final static boolean isValidEmail(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }
    }

    @Override
    public void onResponse(Call<ApiResponse<RegistrationResponse>> call, Response<ApiResponse<RegistrationResponse>> response) {
        if(response.code() == 201 && response.body().success){
            this.settings.startSession(response.body().content.token, response.body().content.user, true );
            Intent intent = new Intent(this, EmailVerificationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<RegistrationResponse>> call, Throwable t) {
        Log.d("coach", "error registering user");
    }
}
