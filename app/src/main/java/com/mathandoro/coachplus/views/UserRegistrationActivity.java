package com.mathandoro.coachplus.views;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.ApiError;
import com.mathandoro.coachplus.api.ApiErrorUtils;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.helpers.ApiErrorResolver;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
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
    TextInputLayout passwordRepeatTextInputLayout;
    Button registrationButton;

    TextView dataPrivacyTextView;
    TextView termsOfUseTextView;

    SwitchCompat dataPrivacyToggle;
    SwitchCompat termsOfUseToggle;

    Settings settings;

    String firstname;
    String lastname;
    String email;
    String password;
    String passwordRepeat;
    boolean termsAccepted;
    boolean dataPrivacyAccepted;


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
        passwordRepeatTextInputLayout = findViewById(R.id.user_registration_password_repeat);
        registrationButton = findViewById(R.id.user_registration_register_button);
        setRegistrationEnabled(false);

        dataPrivacyTextView = findViewById(R.id.user_registration_data_privacy_link);
        termsOfUseTextView = findViewById(R.id.user_registration_terms_of_use_link);

        String termsOfUseText = getResources().getString(R.string.You_must_agree_to_the_terms_and_conditions);
        String termsOfUseLink = "https://coach.plus/terms-of-use";
        termsOfUseTextView.setText(Html.fromHtml(  " <a href=\"" + termsOfUseLink + "\">" + termsOfUseText + "</a>"));
        termsOfUseTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        termsOfUseTextView.setTextColor(Color.WHITE);

        String dataPrivacyText = getResources().getString(R.string.You_must_agree_to_the_dataprivacy_policy);
        String dataPrivacyLink = "https://coach.plus/data-privacy";
        dataPrivacyTextView.setText(Html.fromHtml(  " <a href=\"" + dataPrivacyLink + "\">" + dataPrivacyText + "</a>"));
        dataPrivacyTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        dataPrivacyTextView.setTextColor(Color.WHITE);


        dataPrivacyToggle = findViewById(R.id.user_registration_data_privacy_toggle);
        termsOfUseToggle = findViewById(R.id.user_registration_terms_of_use_toggle);

        dataPrivacyToggle.setOnCheckedChangeListener((buttonView, isChecked) -> updateRegistrationButtonState());
        termsOfUseToggle.setOnCheckedChangeListener((buttonView, isChecked) -> updateRegistrationButtonState());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateRegistrationButtonState();
                if(editable instanceof EditText){
                    ((EditText) editable).setError(null);
                }
                if(editable == passwordRepeatEditText.getEditableText()){
                    passwordRepeatTextInputLayout.setPasswordVisibilityToggleEnabled(true);
                }
            }
        };

        firstnameEditText.addTextChangedListener(textWatcher);
        firstnameEditText.addTextChangedListener(textWatcher);
        lastnameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        passwordRepeatEditText.addTextChangedListener(textWatcher);

        registrationButton.setOnClickListener(view -> registerUser());
    }

    private void updateRegistrationButtonState(){
        readInputFields();
        boolean emptyFields = (TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty((passwordRepeat)));
        setRegistrationEnabled(!emptyFields && termsOfUseToggle.isChecked() && dataPrivacyToggle.isChecked());
    }

    private void setRegistrationEnabled(boolean enabled){
        if(enabled){
            registrationButton.setVisibility(View.VISIBLE);
        }
        else {
            registrationButton.setVisibility(View.INVISIBLE);
        }
    }

    private void readInputFields(){
        firstname = firstnameEditText.getText().toString();
        lastname = lastnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        passwordRepeat = passwordRepeatEditText.getText().toString();
        dataPrivacyAccepted = dataPrivacyToggle.isChecked();
        termsAccepted = termsOfUseToggle.isChecked();
    }

    public void registerUser(){
        readInputFields();
        String repeatPassword = passwordRepeatEditText.getText().toString();
        String pleaseFillInThisField = getResources().getString(R.string.Please_fill_in_this_field);

        if(TextUtils.isEmpty(firstname)){
            firstnameEditText.setError(pleaseFillInThisField);
            return;
        }

        if(TextUtils.isEmpty(lastname)){
            lastnameEditText.setError(pleaseFillInThisField);
            return;
        }

        if(!this.isValidEmail(email)){
            emailEditText.setError(getResources().getString(R.string.Please_enter_your_email_address));
            return;
        }

        if(!password.equals(repeatPassword)){
            passwordRepeatEditText.setError(getResources().getString(R.string.Passwords_must_match));
            passwordRepeatTextInputLayout.setPasswordVisibilityToggleEnabled(false);
            return;
        }

        Call<ApiResponse<RegistrationResponse>> registerUserCall = ApiClient.instance().userService.registerUser(new RegisterUser(firstname, lastname, email, password, termsAccepted, dataPrivacyAccepted));
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
        if(response.isSuccessful()){
            this.settings.startSession(response.body().content.token, response.body().content.user, true );
            Intent intent = new Intent(this, EmailVerificationActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            ApiError apiError = ApiErrorUtils.parseErrorResponse(response);
            SnackbarHelper.showError(firstnameEditText, apiError.getMessage());
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<RegistrationResponse>> call, Throwable t) {
        Log.d("coach", "error registering user");
    }
}
