package com.mathandoro.coachplus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.data.DataLayerCallback;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView eventDetailRecyclerView;
    private EventDetailAdapter eventDetailAdapter;
    private DataLayer dataLayer;

    List<TeamMember> teamMembers;
    Event event;
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        team = getIntent().getExtras().getParcelable("team");
        event = getIntent().getExtras().getParcelable("event");

        dataLayer = DataLayer.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        loadEventDetailRecyclerView();
        loadData();
    }

    private void loadEventDetailRecyclerView(){
        eventDetailRecyclerView = (RecyclerView) findViewById(R.id.event_detail_recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        eventDetailRecyclerView.setLayoutManager(mLinearLayoutManager);
        eventDetailAdapter = new EventDetailAdapter(this, event);
        eventDetailRecyclerView.setAdapter(eventDetailAdapter);
    }

    private void loadData(){
        this.loadTeamMembers();
    }

    private void loadTeamMembers(){
        this.dataLayer.getTeamMembers(team, true, new DataLayerCallback<List<TeamMember>>() {
            @Override
            public void dataChanged(List<TeamMember> members) {
                teamMembers = members;
                eventDetailAdapter.setMembers(members);
            }

            @Override
            public void error() {
            }
        });
    }


}
