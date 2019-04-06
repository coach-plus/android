package com.mathandoro.coachplus.views.TeamView;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.viewHolders.EventItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;
import com.mathandoro.coachplus.views.viewHolders.TeamImageItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.TeamMemberViewHolder;
import com.mathandoro.coachplus.views.viewHolders.UpcomingEventsHeaderViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by dominik on 02.04.17.
 */

public class TeamViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final TeamViewActivity mainActivity;
    private final TeamViewFragment teamFeedFragment;
    private List<TeamMember> members;
    private List<Event> visibleEvents;
    private JWTUser myUser;
    private Membership myUsersMembership;
    private DataLayer dataLayer;
    private RecycleViewStack viewStack;

    final int UPCOMING_EVENTS_HEADER = 0;
    final int UPCOMING_EVENTS_ITEM = 1;
    final int MEMBERS_HEADER = 2;
    final int MEMBERS_ITEM = 3;
    final int TEAM_IMAGE_ITEM = 4;
    final int NO_UPCOMING_EVENTS = 5;
    final int FOOTER = 6;

    private static final int MAX_VISIBLE_EVENTS = 3;


    public TeamViewAdapter(TeamViewActivity mainActivity, TeamViewFragment teamFeedFragment, Membership myUsersMembership) {
        this.members = new ArrayList<>();
        this.myUsersMembership = myUsersMembership;
        this.visibleEvents = new ArrayList<>();
        this.teamFeedFragment = teamFeedFragment;
        this.mainActivity = mainActivity;
        this.dataLayer = DataLayer.getInstance(mainActivity);
        this.loadMyUser();

        viewStack = new RecycleViewStack();
        viewStack.addSection(TEAM_IMAGE_ITEM, 1);
        viewStack.addSection(UPCOMING_EVENTS_HEADER, 1);
        viewStack.addSection(UPCOMING_EVENTS_ITEM, 0);
        viewStack.addSection(NO_UPCOMING_EVENTS, 0);
        viewStack.addSection(MEMBERS_HEADER, 1);
        viewStack.addSection(MEMBERS_ITEM, 0);
        viewStack.addSection(FOOTER, 1);
    }

    private void loadMyUser(){
        Observable<MyUserResponse> myUserV2 = this.dataLayer.getMyUserV2(true);
        myUserV2.subscribe((response) -> this.onMyUserChanged(response.user));
    }

    private void onMyUserChanged(JWTUser myUser){
        this.myUser = myUser;
        this.notifyDataSetChanged();
    }

    public void setMembers(List<TeamMember> members){
        this.members = members;
        this.viewStack.updateSection(MEMBERS_ITEM, members.size());
        this.notifyDataSetChanged();
    }

    public void setUpcomingEvents(List<Event> events){
        this.visibleEvents = this.filterVisibleEvents(events);
        if(this.visibleEvents.size() == 0){
            this.viewStack.updateSection(NO_UPCOMING_EVENTS, 1);
            this.viewStack.updateSection(UPCOMING_EVENTS_ITEM, 0);
        }
        else{
            this.viewStack.updateSection(NO_UPCOMING_EVENTS, 0);
            this.viewStack.updateSection(UPCOMING_EVENTS_ITEM, this.visibleEvents.size());
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewStack.getSectionIdAt(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view;
        switch(viewType){
            case TEAM_IMAGE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_team_image_item, parent, false);
                return new TeamImageItemViewHolder(view);
            case UPCOMING_EVENTS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_upcoming_events_header, parent, false);
                return new UpcomingEventsHeaderViewHolder(view);
            case UPCOMING_EVENTS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventItemViewHolder(view);
            case MEMBERS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_members_header, parent, false);
                return new StaticViewHolder(view);
            case NO_UPCOMING_EVENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_no_event_item, parent, false);
                return new StaticViewHolder(view);
            case MEMBERS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
                if(myUsersMembership.isCoach()){
                    View.inflate(view.getContext(), R.layout.member_item_actions_indicator, view.findViewById(R.id.member_item_right_container));
                }
                return new TeamMemberViewHolder(view);
            case FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                return new StaticViewHolder(view);
        }
        throw new Error("unknown viewType: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_IMAGE_ITEM:
                String image = myUsersMembership.getTeam().getImage(); // this.teamFeedFragment.getCurrentMembership().getTeam().getImage();
                TeamImageItemViewHolder teamImageItemViewHolder = (TeamImageItemViewHolder) holder;
                if(image != null){
                    String imageUrl = BuildConfig.BASE_URL + "/uploads/" + image;
                     Picasso.with(teamImageItemViewHolder.itemView.getContext())
                            .load(imageUrl)
                            .placeholder(teamImageItemViewHolder.teamImage.getDrawable())
                            .resize(Settings.TEAM_ICON_LARGE, Settings.TEAM_ICON_LARGE)
                             .into(teamImageItemViewHolder.teamImage);
                }
                else{
                    teamImageItemViewHolder.teamImage.setImageResource(R.drawable.circle);
                }

                break;
            case UPCOMING_EVENTS_HEADER:
                UpcomingEventsHeaderViewHolder upcomingEventsHeaderViewHolder = (UpcomingEventsHeaderViewHolder) holder;
                upcomingEventsHeaderViewHolder.seeAllEventsButton.setOnClickListener((View view) -> {
                    teamFeedFragment.navigateToAllEvents();
                });
                break;

            case UPCOMING_EVENTS_ITEM:
                EventItemViewHolder eventItemViewHolder = (EventItemViewHolder)holder;
                Event event = getEvent(position);
                eventItemViewHolder.bind(event);
                holder.itemView.setOnClickListener((View view) ->
                    teamFeedFragment.navigateToEvent(event)
                );
                break;

            case MEMBERS_ITEM:
                TeamMemberViewHolder memberViewHolder = (TeamMemberViewHolder)holder;
                final TeamMember teamMember = getMember(position);
                memberViewHolder.bindTeamViewMode(teamMember, myUser,
                        () -> mainActivity.showBottomSheet(teamMember), myUsersMembership.isCoach());
                memberViewHolder.itemView.setOnClickListener((View view) ->
                    teamFeedFragment.navigateToUserProfile(teamMember.getUser()));
                break;
        }
    }

    protected Event getEvent(int position){
        return this.visibleEvents.get(viewStack.positionInSection(UPCOMING_EVENTS_ITEM, position));
    }

    protected TeamMember getMember(int position){
        return this.members.get(viewStack.positionInSection(MEMBERS_ITEM, position));
    }

    private List<Event> filterVisibleEvents(List<Event> events){
        Collections.sort(events, (eventA, eventB) -> eventA.getStart().compareTo(eventB.getStart()));
        List<Event> upcomingEvents = new ArrayList();
        for(Event event: events){
            if(event.getEnd().after(new Date())){
                upcomingEvents.add(event);
                if(upcomingEvents.size() == MAX_VISIBLE_EVENTS){
                    break;
                }
            }
        }
        return upcomingEvents;
    }

    @Override
    public int getItemCount() {
        return viewStack.size();
    }

    public void setMembership(Membership updatedMembership) {
        this.myUsersMembership = updatedMembership;
        notifyDataSetChanged();
    }
}