package com.mathandoro.coachplus.views;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
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
        disableAutoFill();

        setContentView(R.layout.reset_password_activity);
        emailTextInput = findViewById(R.id.reset_password_email_text_input);
        dataLayer = new DataLayer(this);
    }

    public void resetPassword(View view) {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(emailTextInput.getText().toString());
        dataLayer.resetPassword(resetPasswordRequest).subscribe(
                response -> passwordResetSuccess(),
                error -> SnackbarHelper.showText(emailTextInput, R.string.Error));
    }

    private void passwordResetSuccess(){
        setResult(RESULT_OK);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutoFill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }
}
