package com.mathandoro.coachplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.data.DataLayerCallback;
import com.mathandoro.coachplus.models.Membership;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NoTeamsFragment.NoTeamsFragmentListener, ToolbarFragment.ToolbarFragmentListener {

    Settings settings;
    private MyMembershipsAdapter myMembershipsAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    DataLayer dataLayer;
    private DrawerLayout drawer;
    private boolean initalMembershipsLoaded;
    protected List<Membership> memberships;
    protected ToolbarFragment toolbarFragment;

    static int CREATE_TEAM_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initalMembershipsLoaded = false;
        this.settings = new Settings(this);
        setContentView(R.layout.activity_main);

        dataLayer = DataLayer.getInstance(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar);
        toolbarFragment.setListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        this.loadMembershipsRecyclerView();
        this.loadMemberships();
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
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        myMembershipsAdapter = new MyMembershipsAdapter(this);
        mRecyclerView.setAdapter(myMembershipsAdapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        Intent createTeamIntent = new Intent(this, UserProfileActivity.class);
        startActivity(createTeamIntent);
    }

    public void logout(View view){
        this.settings.reset();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchTeamContext(Membership membership) {
        this.settings.setActiveTeamId(membership.getTeam().get_id());
        TeamFeedFragment teamFeedFragment = TeamFeedFragment.newInstance(membership);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, teamFeedFragment)
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
}
