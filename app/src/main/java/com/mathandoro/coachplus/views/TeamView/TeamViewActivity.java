package com.mathandoro.coachplus.views.TeamView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.PreloadLayoutManager;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.views.TeamRegistrationActivity;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.UserProfile.UserProfileActivity;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.LoginActivity;

import java.util.List;

import io.reactivex.Observable;


public class TeamViewActivity extends AppCompatActivity implements NoTeamsFragment.NoTeamsFragmentListener,
        ToolbarFragment.ToolbarFragmentListener, SwipeRefreshLayout.OnRefreshListener {

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
    private SwipeRefreshLayout membershipsSwipeRefreshLayout;

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
                TeamViewActivity.this.navigateToCreateTeamActivity()
        );
        logoutView.setOnClickListener((View v) -> logout());
    }

    private void loadMyUser(){
        Observable<MyUserResponse> myUserV2 = this.dataLayer.getMyUserV2(true);
        myUserV2.subscribe((response) -> myUser = response.user);
    }

    private void logout(){
        settings.reset();
        // todo clear cached files
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
    }

    private void loadMemberships(){
        this.dataLayer.getMyMembershipsV2(false).subscribe(myMembershipsResponse -> {
            membershipsSwipeRefreshLayout.setRefreshing(false);
            memberships = myMembershipsResponse.getMemberships();
            myMembershipsAdapter.setMemberships(myMembershipsResponse.getMemberships());
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
        Intent createTeamIntent = new Intent(this, TeamRegistrationActivity.class);
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
        mRecyclerView = findViewById(R.id.memberships_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        membershipsSwipeRefreshLayout = findViewById(R.id.memberships_swipe_layout);
        membershipsSwipeRefreshLayout.setOnRefreshListener(this);

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
        return true;
    }

    public void switchTeamContext(Membership membership) {
        toolbarFragment.setTeam(membership.getTeam());
        this.settings.setActiveTeamId(membership.getTeam().get_id());
        TeamViewFragment fragment = TeamViewFragment.newInstance(membership);
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
        this.navigateToMyUserProfile();
    }

    public void navigateToMyUserProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user", myUser);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        this.loadMemberships();
    }
}

