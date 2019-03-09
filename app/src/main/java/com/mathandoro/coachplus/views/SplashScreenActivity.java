package com.mathandoro.coachplus.views;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;


public class SplashScreenActivity extends AppCompatActivity {

    Settings settings;
    String TAG = "coach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

//        this.settings = new Settings(this);
//
//        if(settings.getToken() != null){
//            this.navigateToMainActivity();
//        }
        initFirebase();
    }

    private void initFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                    //    String deviceId = task.getResult().getId(); ?correct ?

                        // Log and toast
                        // todo use String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        Toast.makeText(SplashScreenActivity.this, token, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void navigateToMainActivity(){
        Intent intent = new Intent(this, TeamViewActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View view) {
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
