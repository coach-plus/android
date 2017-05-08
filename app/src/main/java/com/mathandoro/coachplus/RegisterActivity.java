package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.Response.ApiResponse;
import com.mathandoro.coachplus.models.RegisterUser;
import com.mathandoro.coachplus.models.Response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity implements Callback<ApiResponse<RegistrationResponse>> {


    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordRepeatEditText;
    Settings settings;

    String firstname;
    String lastname;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.settings = new Settings(this);

        firstnameEditText = ((EditText)findViewById(R.id.firstnameEditText));
        lastnameEditText = ((EditText)findViewById(R.id.lastnameEditText));
        emailEditText = ((EditText)findViewById(R.id.emailEditText));
        passwordEditText = ((EditText)findViewById(R.id.passwordEditText));
        passwordRepeatEditText = ((EditText)findViewById(R.id.repeatPasswordEditText));
    }

    public void registerUser(View view){
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
            this.settings.setToken(response.body().content.token);
            this.settings.setFirstname(this.firstname);
            this.settings.setLastname(this.lastname);
            this.settings.setEmail(this.email);
            this.settings.setPassword(this.password);

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
