package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 02.04.17.
 */

public class TeamFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainActivity mainActivity;
    private List<User> members;
    private List<Event> events;

    final int UPCOMING_EVENTS_HEADER = 0;
    final int UPCOMING_EVENTS_ITEM = 1;
    final int MEMBERS_HEADER = 2;
    final int MEMBERS_ITEM = 3;

    class UpcomingEventsHeaderViewHolder extends RecyclerView.ViewHolder {
        public UpcomingEventsHeaderViewHolder(View view) {
            super(view);
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
        public TeamMembersItemViewHolder(View view) {
            super(view);
        }
    }





    public TeamFeedAdapter(MainActivity mainActivity) {
        this.members = new ArrayList<>();
        this.events = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    public void setMembers(List<User> members){
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
            return UPCOMING_EVENTS_HEADER;
        }
        else if(position > 0 && position <= events.size()){
            return UPCOMING_EVENTS_ITEM;
        }
        else if(position == 1 + events.size()){
            return MEMBERS_HEADER;
        }
        return MEMBERS_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType){
            case UPCOMING_EVENTS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_upcoming_events_header, parent, false);
                viewHolder = new UpcomingEventsHeaderViewHolder(view);
                break;
            case UPCOMING_EVENTS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false);
                viewHolder = new UpcomingEventsItemViewHolder(view);
                break;
            case MEMBERS_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_feed_members_header, parent, false);
                viewHolder = new TeamMembersHeaderViewHolder(view);
                break;
            case MEMBERS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false);
                viewHolder = new TeamMembersItemViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case UPCOMING_EVENTS_ITEM:
                UpcomingEventsItemViewHolder eventItemViewHolder = (UpcomingEventsItemViewHolder)holder;
                Event event = getEvent(position);
                break;

            case MEMBERS_ITEM:
                TeamMembersItemViewHolder memberViewHolder = (TeamMembersItemViewHolder)holder;
                break;
        }
    }

    protected Event getEvent(int position){
        return this.events.get(position -1);
    }

    protected User getMember(int position){
        return this.members.get(position - 2 - events.size());
    }

    @Override
    public int getItemCount() {
        return members.size() + events.size() + 2;
    }
}