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
        //    this.navigateToMainActivity();
        }
        DataLayer dataLayer = DataLayer.getInstance(this);
        Cache cache = new Cache(this);
        List<Membership> membershipList = new ArrayList<>();
        List<Membership> loadedMembershipList;
        Team team = new Team("123.1231", "teamname", false);
        membershipList.add(new Membership("admin", team, "user"));
        membershipList.add(new Membership("admin2", team, "user2"));

        try {
            cache.saveList(membershipList, "test", CacheContext.DEFAULT());
            Log.d("cache", "saving successful");
            loadedMembershipList = cache.readList("test", CacheContext.DEFAULT(), Membership.class);
            Log.d("cache", "loading successful");
        } catch (IOException e) {
            e.printStackTrace();
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
