package com.mathandoro.coachplus.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Request.UpdatePasswordRequest;
import com.mathandoro.coachplus.api.Request.UpdateUserRequest;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import androidx.appcompat.app.AppCompatActivity;

public class UserSettingsActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    private ToolbarFragment toolbarFragment;
    private DataLayer dataLayer;
    private TextInputEditText oldPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText newPasswordRepeatInput;
    private TextInputEditText firstnameInput;
    private TextInputEditText lastnameInput;
    private TextInputEditText emailInput;
    private Settings settings;
    private Button saveUserInformationButton;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_activity);

        dataLayer = new DataLayer(getApplicationContext());

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_settings_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle(getString(R.string.user_settings_title));

        settings = new Settings(getApplicationContext());

        firstnameInput = findViewById(R.id.user_settings_firstname_text_input);
        lastnameInput = findViewById(R.id.user_settings_lastname_text_input);
        emailInput = findViewById(R.id.user_settings_email_text_input);
        saveUserInformationButton = findViewById(R.id.user_settings_save_user_information_button);

        saveUserInformationButton.setOnClickListener((View view) -> this.saveUserInformation());

        JWTUser user = settings.getUser();
        firstnameInput.setText(user.getFirstname());
        lastnameInput.setText(user.getLastname());
        emailInput.setText(user.getEmail());

        TextWatcher userInformationChangedWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!saveUserInformationButton.isEnabled()){
                    saveUserInformationButton.setEnabled(true);
                }
            }
        };

        firstnameInput.addTextChangedListener(userInformationChangedWatcher);
        lastnameInput.addTextChangedListener(userInformationChangedWatcher);
        emailInput.addTextChangedListener(userInformationChangedWatcher);

        oldPasswordInput = findViewById(R.id.user_settings_old_password_text_input);
        newPasswordInput = findViewById(R.id.user_settings_new_password_text_input);
        newPasswordRepeatInput = findViewById(R.id.user_settings_new_password_repeat_text_input);
        changePasswordButton = findViewById(R.id.user_settings_update_password_button);

        TextWatcher passwordChangeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(oldPasswordInput.getText().toString(),
                        newPasswordInput.getText().toString(), newPasswordRepeatInput.getText().toString());

                changePasswordButton.setEnabled(updatePasswordRequest.isValid());
            }
        };

        newPasswordInput.setOnFocusChangeListener((view, focused) -> {
            if(!focused && newPasswordInput.equals("")){
                newPasswordInput.setError("can't be empty");
            }
        });

        newPasswordRepeatInput.setOnFocusChangeListener((view, focused) -> {
            if(!focused && !newPasswordInput.equals(newPasswordRepeatInput)){
                newPasswordRepeatInput.setError("doesn't match your new password");
            }
        });

        oldPasswordInput.addTextChangedListener(passwordChangeWatcher);
        newPasswordInput.addTextChangedListener(passwordChangeWatcher);
        newPasswordRepeatInput.addTextChangedListener(passwordChangeWatcher);

        changePasswordButton.setOnClickListener((View view) -> this.changePassword());
    }



    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {

    }

    private void changePassword() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(oldPasswordInput.getText().toString(),
                newPasswordInput.getText().toString(), newPasswordRepeatInput.getText().toString());
        dataLayer.updatePassword(updatePasswordRequest).subscribe( (data) -> {
            Snackbar.make(oldPasswordInput, "password changed successfully", Snackbar.LENGTH_SHORT).show();
        }, (error) -> showError());

    }


    private void saveUserInformation(){
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(firstnameInput.getText().toString(),
                lastnameInput.getText().toString(), emailInput.getText().toString());



        dataLayer.updateUserInformation(updateUserRequest).subscribe( (data) -> {
            Snackbar.make(oldPasswordInput, "user information changed successfully", Snackbar.LENGTH_SHORT).show();
            settings.setMyUser(data.user);
            AppState.myUserChanged$.onNext(data.user);

        }, (error) -> showError());
    }

    private void showError(){
        Snackbar.make(oldPasswordInput, "something went wrong. Try again later.", Snackbar.LENGTH_SHORT).show();
    }
}
