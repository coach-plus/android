package com.mathandoro.coachplus.views.EventDetail;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.api.Response.ParticipationResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.helpers.CustomDialog;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.News;
import com.mathandoro.coachplus.models.Participation;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.CreateEventActivity;
import com.mathandoro.coachplus.views.UserProfile.ConfirmationBottomSheet;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;

public class EventDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_BUNDLE = "bundle";
    public static final String EXTRA_TEAM = "team";
    public static final String EXTRA_EVENT = "event";
    public static final String EXTRA_MEMBERSHIP = "membership";

    public static final int EDIT_EVENT_REQUEST = 1;

    private RecyclerView eventDetailRecyclerView;
    private EventDetailAdapter eventDetailAdapter;
    private DataLayer dataLayer;

    private FloatingActionButton createNewsFab;
    private FloatingActionButton editEventFab;
    private FloatingActionButton reminderFab;
    private FloatingActionsMenu floatingActionsMenu;

    private SwipeRefreshLayout swipeRefreshLayout;


    Map<String, ParticipationItem> map = new HashMap<>();
    Event event;
    Team team;
    Membership membership;
    ToolbarFragment toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_activity);
        toolbar = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.create_event_activity_toolbar);
        toolbar.showBackButton();
        toolbar.setListener(new ToolbarFragment.ToolbarFragmentListener() {
            @Override
            public void onLeftIconPressed() {
                finish();
            }

            @Override
            public void onRightIconPressed() {
            }
        });

        swipeRefreshLayout = findViewById(R.id.eventDetailSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Bundle bundle = getIntent().getExtras().getBundle(EXTRA_BUNDLE);

        team = bundle.getParcelable(EXTRA_TEAM);
        event = bundle.getParcelable(EXTRA_EVENT);
        membership = bundle.getParcelable(EXTRA_MEMBERSHIP);

        toolbar.setTitle(event.getName());
        dataLayer = DataLayer.getInstance(this);

        floatingActionsMenu = findViewById(R.id.event_detail_fab_menu);

        createNewsFab = findViewById(R.id.event_detail_create_news_fab);
        createNewsFab.setOnClickListener(view -> this.showCreateNewsDialog());

        editEventFab = findViewById(R.id.event_detail_edit_button);
        editEventFab.setOnClickListener(view -> editEvent());

        reminderFab = findViewById(R.id.event_detail_reminder_button);
        reminderFab.setOnClickListener(view -> sendReminder());

        if(!membership.isCoach()){
            floatingActionsMenu.setVisibility(View.GONE);
        }
        loadEventDetailRecyclerView();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == EDIT_EVENT_REQUEST && data != null){
            String action = data.getExtras().getString(CreateEventActivity.RETURN_INTENT_PARAM_ACTION);
            if(action.equals(CreateEventActivity.ACTION_UPDATED)){
                Event event = data.getExtras().getParcelable(CreateEventActivity.RETURN_INTENT_PARAM_EVENT);
                applyUpdatedEvent(event);

            }
            else if(action.equals(CreateEventActivity.ACTION_DELETED)){
                finish();
                // TODO reload list in event list or upcoming 3 events
            }
        }
    }

    private void applyUpdatedEvent(Event event){
        this.event = event;
        this.eventDetailAdapter.setEvent(event);
        toolbar.setTitle(event.getName());
    }

    private void editEvent(){
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra(CreateEventActivity.INTENT_PARAM_EVENT, event);
        intent.putExtra(CreateEventActivity.INTENT_PARAM_TEAM, team);
        startActivityForResult(intent, EDIT_EVENT_REQUEST);
        floatingActionsMenu.collapse();
    }

    private void sendReminder(){
        dataLayer.sendReminder(team.get_id(), event.get_id()).subscribe(o -> {
            Snackbar.make(reminderFab, getString(R.string.reminder_was_send_snackbar_text), Snackbar.LENGTH_SHORT).show();
        }, throwable -> {});
    }

    private void showCreateNewsDialog(){
        floatingActionsMenu.collapse();

        CustomDialog customDialog = new CustomDialog(this, R.layout.create_news_dialog);
        customDialog.show();
        EditText newsText = (EditText) customDialog.findViewById(R.id.create_news_dialog_news_edit_text);
        customDialog.findViewById(R.id.create_news_dialog_create_news_button).setOnClickListener(view -> {
            customDialog.hide();
            // create event
            dataLayer.createNews(team.get_id(), event.get_id(), "title", newsText.getText().toString()).subscribe(response ->{
                // todo update list instead
                loadNews(); // workaround: load all news again
            });
        });
        customDialog.findViewById(R.id.create_news_dialog_cancel_button).setOnClickListener(view -> {
            customDialog.hide();
        });

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
       this.loadNews();
    }

    private void loadNews(){
        this.dataLayer.getNews(team.get_id(), event.get_id()).subscribe(newsResponse -> eventDetailAdapter.setNews(newsResponse.getNews()));
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

    public void showBottomSheet(String userId, final boolean didAttend){
        EventDetailBottomSheet bottomSheet = new EventDetailBottomSheet();
        bottomSheet.setListener(() -> {
            this.dataLayer.setDidAttend(team.get_id(), event.get_id(), userId, didAttend).subscribe(participation -> {
                updateParticipation(participation);
                bottomSheet.dismiss();
            });
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    public void showNewsDeletionConfirmation(News news){
        ConfirmationBottomSheet bottomSheet = ConfirmationBottomSheet.show(getSupportFragmentManager(), "do you want to delete the messsage?", true);
        bottomSheet.setListener(new ConfirmationBottomSheet.IComfirmationBottomSheetListener() {
            @Override
            public void onConfirm() {
                dataLayer.deleteNews(team.get_id(), event.get_id(), news.getId()).subscribe(news -> {
                    loadNews();
                    bottomSheet.dismiss();
                });
                bottomSheet.dismiss();
            }

            @Override
            public void onDecline() {
                bottomSheet.dismiss();
            }
        });

    }

    @Override
    public void onRefresh() {
        dataLayer.getEvent(this.team.get_id(), this.event.get_id(), false).subscribe(response -> {
            applyUpdatedEvent(response.getEvent());
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
