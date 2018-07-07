package com.mathandoro.coachplus.views.TeamView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.EventList.EventItemViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    class TeamMembersItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView role;
        ImageView icon;

        public TeamMembersItemViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.team_feed_member_name);
            role = view.findViewById(R.id.team_feed_member_role);
            icon = view.findViewById(R.id.team_feed_member_icon);
        }
    }


    public TeamViewAdapter(TeamViewActivity mainActivity, TeamViewFragment teamFeedFragment) {
        this.members = new ArrayList<>();
        this.events = new ArrayList<>();
        this.teamFeedFragment = teamFeedFragment;
        this.mainActivity = mainActivity;
        //AppState.instance(mainActivity).myUser.subscribe((JWTUser user) -> this.onMyUserChanged(user));
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_view_member_item, parent, false);
                return new TeamMembersItemViewHolder(view);
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
                TeamMembersItemViewHolder memberViewHolder = (TeamMembersItemViewHolder)holder;
                final TeamMember teamMember = getMember(position);
                String username = teamMember.getUser().getFirstname() + " " + teamMember.getUser().getLastname();
                if(this.myUser != null && this.myUser.get_id().equals(teamMember.getUser().get_id())){
                    username += " (" + mainActivity.getString(R.string.you) + ")";
                }
                memberViewHolder.name.setText(username);
                memberViewHolder.role.setText(teamMember.getRole());
                String userImage = teamMember.getUser().getImage();
                if(userImage != null){
                    String imageUrl = BuildConfig.BASE_URL + "/uploads/" + userImage;
                    Picasso.with(memberViewHolder.itemView.getContext())
                            .load(imageUrl)
                            .resize(Settings.USER_ICON_SIZE, Settings.USER_ICON_SIZE)
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.circle)
                            .into(memberViewHolder.icon);
                }
                else{
                    memberViewHolder.icon.setImageResource(R.drawable.circle);
                }
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
        List<Event> upcomingEvents = new ArrayList<Event>();
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