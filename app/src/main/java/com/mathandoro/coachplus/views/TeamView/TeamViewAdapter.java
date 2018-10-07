package com.mathandoro.coachplus.views.TeamView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.views.viewHolders.EventItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.TeamMemberViewHolder;
import com.squareup.picasso.Picasso;

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
    private List<Event> events;
    private JWTUser myUser;
    private boolean noUpcomingEvents = false;
    private DataLayer dataLayer;

    final int UPCOMING_EVENTS_HEADER = 0;
    final int UPCOMING_EVENTS_ITEM = 1;
    final int MEMBERS_HEADER = 2;
    final int MEMBERS_ITEM = 3;
    final int TEAM_IMAGE_ITEM = 4;
    final int NO_UPCOMING_EVENTS = 5;

    private static final int MAX_VISIBLE_EVENTS = 3;


    class TeamImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView teamImage;
        public TeamImageItemViewHolder(View view) {
            super(view);
            teamImage = view.findViewById(R.id.team_feed_team_image);
        }
    }

    class UpcomingEventsHeaderViewHolder extends RecyclerView.ViewHolder {
        Button seeAllEventsButton;

        public UpcomingEventsHeaderViewHolder(View view) {
            super(view);
            seeAllEventsButton = view.findViewById(R.id.team_feed_upcoming_events_header_see_all_events_button);
        }
    }


    class TeamMembersHeaderViewHolder extends RecyclerView.ViewHolder {
        public TeamMembersHeaderViewHolder(View view) {
            super(view);
        }
    }

    class NoEventsViewHolder extends RecyclerView.ViewHolder {
        public NoEventsViewHolder(View view) {
            super(view);
        }
    }


    public TeamViewAdapter(TeamViewActivity mainActivity, TeamViewFragment teamFeedFragment) {
        this.members = new ArrayList<>();
        this.events = new ArrayList<>();
        this.teamFeedFragment = teamFeedFragment;
        this.mainActivity = mainActivity;
        this.dataLayer = DataLayer.getInstance(mainActivity);
        this.loadMyUser();
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
        this.notifyDataSetChanged();
    }

    public void setUpcomingEvents(List<Event> events){
        this.events = this.filterVisibleEvents(events);
        if(this.events.size() == 0){
            noUpcomingEvents = true;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TEAM_IMAGE_ITEM;
        }
        else if(position == 1){
            return UPCOMING_EVENTS_HEADER;
        }
        else if(position == 2 && noUpcomingEvents){
            return NO_UPCOMING_EVENTS;
        }
        else if(position > 0){
            int offset = noUpcomingEvents ? 1: 0;
            if(position < events.size() + 2 + offset){
                return UPCOMING_EVENTS_ITEM;
            }
            else if(position == offset + 2 + events.size()){
                return MEMBERS_HEADER;
            }
        }
        return MEMBERS_ITEM;
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
                return new TeamMembersHeaderViewHolder(view);
            case NO_UPCOMING_EVENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_no_event_item, parent, false);
                return new NoEventsViewHolder(view);
            default: //case MEMBERS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
                return new TeamMemberViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_IMAGE_ITEM:
                String image = this.teamFeedFragment.getCurrentMembership().getTeam().getImage();
                TeamImageItemViewHolder teamImageItemViewHolder = (TeamImageItemViewHolder) holder;
                if(image != null){
                    String imageUrl = BuildConfig.BASE_URL + "/uploads/" + image;
                    Picasso.with(teamImageItemViewHolder.itemView.getContext())
                            .load(imageUrl)
                            .resize(Settings.TEAM_ICON_LARGE, Settings.TEAM_ICON_LARGE)
                            .placeholder(R.drawable.circle)
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
                memberViewHolder.bindGeneralInformation(teamMember, myUser);
                memberViewHolder.itemView.setOnClickListener((View view) ->
                    teamFeedFragment.navigateToUserProfile(teamMember.getUser()));
                break;
        }
    }

    protected Event getEvent(int position){
        return this.events.get(position - 2);
    }

    protected TeamMember getMember(int position){
        int offset = noUpcomingEvents ? 1 : 0;
        return this.members.get(position - offset - 3 - events.size());
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
        int noEventSize = noUpcomingEvents ? 1 : 0;
        int teamImage = 1;
        int eventHeader = 1;
        int membersHeader = 1;
        return teamImage
                + members.size()
                + events.size()
                + noEventSize
                + eventHeader
                + membersHeader;
    }
}