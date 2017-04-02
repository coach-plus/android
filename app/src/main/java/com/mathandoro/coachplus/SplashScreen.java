package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mathandoro.coachplus.data.Cache;
import com.mathandoro.coachplus.data.CacheContext;
import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.Membership;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SplashScreen extends AppCompatActivity {

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.settings = new Settings(this);

        if(settings.getToken() != null){
            this.navigateToMainActivity();
        }
    }

    private void navigateToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
