package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.models.RegisterUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity implements Callback<Object> {


    EditText firstnameEditText;
    EditText lastnameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordRepeatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstnameEditText = ((EditText)findViewById(R.id.firstnameEditText));
        lastnameEditText = ((EditText)findViewById(R.id.lastnameEditText));
        emailEditText = ((EditText)findViewById(R.id.emailEditText));
        passwordEditText = ((EditText)findViewById(R.id.passwordEditText));
        passwordRepeatEditText = ((EditText)findViewById(R.id.repeatPasswordEditText));
    }

    public void registerUser(View view){
        String firstname = firstnameEditText.getText().toString();
        String lastname = lastnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
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

        Call<Object> registerUserCall = ApiClient.instance().userService.registerUser(new RegisterUser(firstname, lastname, email, password));
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
    public void onResponse(Call<Object> call, Response<Object> response) {
        if(response.code() == 201){
            Intent intent = new Intent(this, EmailVerificationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailure(Call<Object> call, Throwable t) {
        Log.d("coach", "error registering user");
    }
}
