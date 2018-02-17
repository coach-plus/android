package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.TeamMember;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 02.04.17.
 */

public class TeamFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainActivity mainActivity;
    private final TeamFeedFragment teamFeedFragment;
    private List<TeamMember> members;
    private List<Event> events;

    final int UPCOMING_EVENTS_HEADER = 0;
    final int UPCOMING_EVENTS_ITEM = 1;
    final int MEMBERS_HEADER = 2;
    final int MEMBERS_ITEM = 3;
    final int TEAM_IMAGE_ITEM = 4;


    class TeamImageItemViewHolder extends RecyclerView.ViewHolder {
        ImageView teamImage;
        public TeamImageItemViewHolder(View view) {
            super(view);
            teamImage = (ImageView)view.findViewById(R.id.team_feed_team_image);
        }
    }

    class UpcomingEventsHeaderViewHolder extends RecyclerView.ViewHolder {
        Button seeAllEventsButton;

        public UpcomingEventsHeaderViewHolder(View view) {
            super(view);
            seeAllEventsButton = (Button)view.findViewById(R.id.team_feed_upcoming_events_header_see_all_events_button);
        }
    }

    class UpcomingEventsItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView location;
        TextView time;
        View itemContainer;

        public UpcomingEventsItemViewHolder(View view) {
            super(view);
            this.itemContainer = view;
        }
    }

    class TeamMembersHeaderViewHolder extends RecyclerView.ViewHolder {
        public TeamMembersHeaderViewHolder(View view) {
            super(view);
        }
    }

    class TeamMembersItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView role;
        ImageView icon;
        public TeamMembersItemViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.team_feed_member_name);
            role = (TextView)view.findViewById(R.id.team_feed_member_role);
            icon = (ImageView)view.findViewById(R.id.team_feed_member_icon);
        }
    }


    public TeamFeedAdapter(MainActivity mainActivity, TeamFeedFragment teamFeedFragment) {
        this.members = new ArrayList<>();
        this.events = new ArrayList<>();
        this.teamFeedFragment = teamFeedFragment;
        this.mainActivity = mainActivity;
    }

    public void setMembers(List<TeamMember> members){
        this.members = members;
        this.notifyDataSetChanged();
    }

    public void setUpcomingEvents(List<Event> events){
        this.events = events;
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
        else if(position > 0 && position <= events.size()){
            return UPCOMING_EVENTS_ITEM;
        }
        else if(position == 2 + events.size()){
            return MEMBERS_HEADER;
        }
        return MEMBERS_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = null;
        switch(viewType){
            case TEAM_IMAGE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_team_image_item, parent, false);
                return new TeamImageItemViewHolder(view);
            case UPCOMING_EVENTS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_upcoming_events_header, parent, false);
                return new UpcomingEventsHeaderViewHolder(view);
            case UPCOMING_EVENTS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false);
                return new UpcomingEventsItemViewHolder(view);
            case MEMBERS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_members_header, parent, false);
                return new TeamMembersHeaderViewHolder(view);
            default: //case MEMBERS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_member_item, parent, false);
                return new TeamMembersItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_IMAGE_ITEM:
                TeamImageItemViewHolder teamImageItemViewHolder = (TeamImageItemViewHolder) holder;
                String imageUrl = BuildConfig.BASE_URL + "/uploads/" + this.teamFeedFragment.getCurrentMembership().getTeam().getImage();
                Picasso.with(teamImageItemViewHolder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.circle)
                        .into(teamImageItemViewHolder.teamImage);
                break;
            case UPCOMING_EVENTS_HEADER:
                UpcomingEventsHeaderViewHolder upcomingEventsHeaderViewHolder = (UpcomingEventsHeaderViewHolder) holder;
                upcomingEventsHeaderViewHolder.seeAllEventsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        teamFeedFragment.navigateToAllEvents();
                    }
                });
                break;

            case UPCOMING_EVENTS_ITEM:
                UpcomingEventsItemViewHolder eventItemViewHolder = (UpcomingEventsItemViewHolder)holder;
                Event event = getEvent(position);
                break;

            case MEMBERS_ITEM:
                TeamMembersItemViewHolder memberViewHolder = (TeamMembersItemViewHolder)holder;
                TeamMember teamMember = getMember(position);
                memberViewHolder.name.setText(teamMember.getUser().getFirstname() + " " + teamMember.getUser().getLastname());
                memberViewHolder.role.setText(teamMember.getRole());
                break;
        }
    }

    protected Event getEvent(int position){
        return this.events.get(position -1);
    }

    protected TeamMember getMember(int position){
        return this.members.get(position - 2 - events.size());
    }

    @Override
    public int getItemCount() {
        return members.size() + events.size() + 2;
    }
}