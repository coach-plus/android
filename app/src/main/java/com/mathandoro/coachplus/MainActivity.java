package com.mathandoro.coachplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mathandoro.coachplus.models.Team;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Settings settings;
    private MyMembershipsAdapter myMembershipsAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    DataLayer dataLayer;
    private DrawerLayout drawer;
    private boolean initalMembershipsLoaded;
    protected List<Membership> memberships;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initalMembershipsLoaded = false;
        this.settings = new Settings(this);
        setContentView(R.layout.activity_main);

        dataLayer = DataLayer.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.loadMembershipsRecyclerView();
        this.loadMemberships();
    }

    private void loadMemberships(){
        this.dataLayer.getMyMemberships(new DataLayerCallback<List<Membership>>() {
            @Override
            public void dataChanged(List<Membership> data) {
                memberships = data;
                myMembershipsAdapter.setMemberships(data);
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
        NoTeamsFragment noTeamsFragment = NoTeamsFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, noTeamsFragment)
                .commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
