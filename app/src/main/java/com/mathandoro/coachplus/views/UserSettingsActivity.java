package com.mathandoro.coachplus.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Request.UpdatePasswordRequest;
import com.mathandoro.coachplus.api.Request.UpdateUserRequest;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.layout.FontAwesomeView;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class UserSettingsActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener, SwipeRefreshLayout.OnRefreshListener {

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
    private Button resendEmailButton;
    private MyReducedUser myUser;
    private TextView resendEmailTextView ;
    private ImageView resendEmailBackground;
    private FontAwesomeView resendEmailWarningIcon;

    private SwipeRefreshLayout  swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_activity);

        dataLayer = new DataLayer(this);

        swipeRefreshLayout = findViewById(R.id.user_settings_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_settings_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle(getString(R.string.User_Settings));

        settings = new Settings(getApplicationContext());

        firstnameInput = findViewById(R.id.user_settings_firstname_text_input);
        lastnameInput = findViewById(R.id.user_settings_lastname_text_input);
        emailInput = findViewById(R.id.user_settings_email_text_input);
        saveUserInformationButton = findViewById(R.id.user_settings_save_user_information_button);

        saveUserInformationButton.setOnClickListener((View view) -> this.saveUserInformation());

        myUser = settings.getUser();

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

        resendEmailButton = findViewById(R.id.user_settings_resend_email_button);
        resendEmailTextView = findViewById(R.id.user_settings_resend_email_text);
        resendEmailBackground = findViewById(R.id.user_settings_resend_verification_email_background);
        resendEmailWarningIcon = findViewById(R.id.user_settings_resend_email_icon);

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
            if(!focused && newPasswordInput.getText().equals("")){
                newPasswordInput.setError(getString(R.string.Please_fill_in_this_field));
            }
        });

        newPasswordRepeatInput.setOnFocusChangeListener((view, focused) -> {
            if(!focused && !newPasswordInput.getText().equals(newPasswordRepeatInput.getText())){
                newPasswordRepeatInput.setError(getString(R.string.Passwords_must_match));
            }
        });

        oldPasswordInput.addTextChangedListener(passwordChangeWatcher);
        newPasswordInput.addTextChangedListener(passwordChangeWatcher);
        newPasswordRepeatInput.addTextChangedListener(passwordChangeWatcher);

        changePasswordButton.setOnClickListener((View view) -> this.changePassword());
        resendEmailButton.setOnClickListener(v -> this.resendVerificationEmail());

        initView();
    }

    private void initView(){
        if(myUser.isEmailVerified()){
            resendEmailButton.setVisibility(View.GONE);
            resendEmailTextView.setVisibility(View.GONE);
            resendEmailBackground.setVisibility(View.GONE);
            resendEmailWarningIcon.setVisibility(View.GONE);
        }

        firstnameInput.setText(myUser.getFirstname());
        lastnameInput.setText(myUser.getLastname());
        emailInput.setText(myUser.getEmail());
    }



    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {

    }

    private void resendVerificationEmail(){
        dataLayer.resendEmailVerification().subscribe(
                (data) -> SnackbarHelper.showText(firstnameInput, R.string.Success),
                (error) -> showError());
    }

    private void changePassword() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(oldPasswordInput.getText().toString(),
                newPasswordInput.getText().toString(), newPasswordRepeatInput.getText().toString());
        dataLayer.updatePassword(updatePasswordRequest).subscribe(
                (data) -> passwordChangeSuccessful(),
                (error) -> showError());
    }

    private void passwordChangeSuccessful(){
        SnackbarHelper.showText(oldPasswordInput, R.string.Success);
        oldPasswordInput.setText("");
        newPasswordInput.setText("");
        newPasswordRepeatInput.setText("");

        oldPasswordInput.clearFocus();
        newPasswordInput.clearFocus();
        newPasswordRepeatInput.clearFocus();

        oldPasswordInput.setError(null);
        newPasswordInput.setError(null);
        newPasswordRepeatInput.setError(null);

        changePasswordButton.setEnabled(false);
    }


    private void saveUserInformation(){
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(firstnameInput.getText().toString(),
                lastnameInput.getText().toString(), emailInput.getText().toString());
                dataLayer.updateUserInformation(updateUserRequest).subscribe( (data) -> {
                    Snackbar.make(oldPasswordInput, getString(R.string.User_Profile_was_updated_successfully), Snackbar.LENGTH_SHORT).show();
                    notifyUserChanged(data.user);
                }, (error) -> showError());
    }

    private void showError(){
        Snackbar.make(oldPasswordInput, R.string.Error, Snackbar.LENGTH_SHORT).show();
    }

    private void notifyUserChanged(MyReducedUser user){
        settings.setMyUser(user);
        AppState.instance().myUserChanged$.onNext(user);
        initView();
    }

    @Override
    public void onRefresh() {
        dataLayer.getMyUserV2(false).subscribe(myUserResponse -> {
            notifyUserChanged(myUserResponse.user);
            swipeRefreshLayout.setRefreshing(false);
        },  throwable ->  showError());
    }
}
