package com.mathandoro.coachplus.views.EventDetail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView eventDetailRecyclerView;
    private EventDetailAdapter eventDetailAdapter;
    private DataLayer dataLayer;

    List<TeamMember> teamMembers;
    List<ParticipationResponse.ParticipationAndMembership> participations;
    Map<String, ParticipationItem> map = new HashMap<>();
    Event event;
    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);
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
        eventDetailRecyclerView = findViewById(R.id.event_detail_recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        eventDetailRecyclerView.setLayoutManager(mLinearLayoutManager);
        eventDetailAdapter = new EventDetailAdapter(this, event);
        eventDetailRecyclerView.setAdapter(eventDetailAdapter);
    }

    private void loadData(){
       Observable.zip(this.loadTeamMembers(), this.loadParticipation(), (teamMembersResponse, participationResponse) -> {
            map = new HashMap<>();
            for (TeamMember teamMember : teamMembersResponse.getMembers()) {
                map.put(teamMember.getUser().get_id(), new ParticipationItem(teamMember, null));
            }
            for (ParticipationResponse.ParticipationAndMembership participationAndMembership : participationResponse.participation) {
                map.get(participationAndMembership.user.get_id()).participation = participationAndMembership.participation;
            }
            return map.values();
        }).subscribe(entries -> {
            eventDetailAdapter.setParticipationItems(new ArrayList<>(entries));
       });
    }


    private Observable<TeamMembersResponse> loadTeamMembers(){
        return this.dataLayer.getTeamMembersV2(true, team);
    }


    protected Observable<ParticipationResponse> loadParticipation(){
       return this.dataLayer.getParticipationOfEvent(false, this.team.get_id(), this.event.get_id());
    }


    public void onUpdateWillAttend(String userId, boolean willAttend) {
        this.dataLayer.setWillAttend(this.team.get_id(), this.event.get_id(), userId, willAttend).subscribe(participation -> {
            this.updateParticipation(participation);
        });
    }

    public void onUpdateDidAttend(String userId, boolean didAttend) {
        this.dataLayer.setDidAttend(this.team.get_id(), this.event.get_id(), userId, didAttend).subscribe(participation -> {
            this.updateParticipation(participation);
        });
    }

    private void updateParticipation(Participation participation){
        this.map.get(participation.getUser()).participation = participation;
        eventDetailAdapter.setParticipationItems(new ArrayList<>(map.values()));
    }
}
