package com.mathandoro.coachplus.views;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.api.Request.ResetPasswordRequest;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
import com.mathandoro.coachplus.persistence.DataLayer;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordResetActivity extends AppCompatActivity {

    private DataLayer dataLayer;
    private TextInputEditText emailTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        emailTextInput = findViewById(R.id.reset_password_email_text_input);
        dataLayer = new DataLayer(this);
    }

    public void resetPassword(View view) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(emailTextInput.getText().toString());
        dataLayer.resetPassword(resetPasswordRequest).subscribe(
                response -> finish(),
                error -> SnackbarHelper.showError(emailTextInput, R.string.error_occurred));
    }
}
