package com.mathandoro.coachplus.views.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.PreloadLayoutManager;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.views.RegisterTeamActivity;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.UserProfile.UserProfileActivity;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.LoginActivity;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NoTeamsFragment.NoTeamsFragmentListener,
        ToolbarFragment.ToolbarFragmentListener {

    Settings settings;
    private MyMembershipsAdapter myMembershipsAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    DataLayer dataLayer;
    private DrawerLayout drawer;
    private boolean initalMembershipsLoaded;
    protected ToolbarFragment toolbarFragment;

    protected List<Membership> memberships;
    protected JWTUser myUser;

    static int CREATE_TEAM_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initalMembershipsLoaded = false;
        this.settings = new Settings(this);
        setContentView(R.layout.activity_main);

        dataLayer = DataLayer.getInstance(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showUserIcon();

        drawer = findViewById(R.id.drawer_layout);

        this.loadNavigationDrawer();
        this.loadMembershipsRecyclerView();
        this.loadMemberships();
        this.loadMyUser();
    }

    private void loadNavigationDrawer(){
        ImageView registerTeamImage = findViewById(R.id.registerTeam);
        TextView logoutView = findViewById(R.id.logout_text_view);
        registerTeamImage.setOnClickListener((View v) ->
                MainActivity.this.navigateToCreateTeamActivity()
        );
        logoutView.setOnClickListener((View v) -> {
            logout();
        });
    }

    private void loadMyUser(){
        this.dataLayer.getMyUser(true, (response) -> {
            myUser = response.user;
        });
    }

    private void logout(){
        settings.reset();
        // todo clear cached files
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
    }


    private void loadMemberships(){
        this.dataLayer.getMyMemberships(new DataLayerCallback<List<Membership>>() {
            @Override
            public void dataChanged(List<Membership> data) {
                memberships = data;
                myMembershipsAdapter.setMemberships(data);
                String activeTeamId = settings.getActiveTeamId();
                if(memberships.size() > 0 && activeTeamId == null){
                    switchTeamContext(memberships.get(0));
                    initalMembershipsLoaded = true;
                    return;
                }
                if (!initalMembershipsLoaded) {
                    initalMembershipsLoaded = true;
                    loadActiveTeam();
                }
            }

            @Override
            public void error() {
            }
        });
    }

    private void loadActiveTeam(){
        String activeTeamId = this.settings.getActiveTeamId();
        if(activeTeamId != null) {
            for (Membership m : memberships) {
                if (m.getTeam().get_id().equals(activeTeamId)){
                    this.switchTeamContext(m);
                    return;
                }
            }
        }
        NoTeamsFragment noTeamsFragment = NoTeamsFragment.newInstance(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, noTeamsFragment)
                .commit();
    }

    public void navigateToCreateTeamActivity(){
        Intent createTeamIntent = new Intent(this, RegisterTeamActivity.class);
        startActivityForResult(createTeamIntent, CREATE_TEAM_REQUEST);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_TEAM_REQUEST && resultCode == RESULT_OK){
           this.loadMemberships();
        }
    }

    private void loadMembershipsRecyclerView(){
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mLinearLayoutManager = new PreloadLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        myMembershipsAdapter = new MyMembershipsAdapter(this);
        mRecyclerView.setAdapter(myMembershipsAdapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_userprofile) {
            this.navigateToUserProfileActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToUserProfileActivity(){
        // todo add get and pass user id to profile activity !
        // todo use toolbar only ?
        // crashes!
        Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
        startActivity(userProfileIntent);
    }

    public void logout(View view){
        this.settings.reset();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchTeamContext(Membership membership) {
        toolbarFragment.setTeam(membership.getTeam());
        this.settings.setActiveTeamId(membership.getTeam().get_id());
        TeamFeedFragment fragment = TeamFeedFragment.newInstance(membership);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onRegisterTeamButtonPressed() {
        drawer.closeDrawer(GravityCompat.START);
        this.navigateToCreateTeamActivity();
    }

    @Override
    public void onLeftIconPressed() {
        drawer.openDrawer(Gravity.START);
    }


    @Override
    public void onRightIconPressed() {
    }
}

