package com.mathandoro.coachplus;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mathandoro.coachplus.models.Event;
import java.util.ArrayList;
import java.util.List;



public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventsActivity eventsActivity;
    private final EventListFragment eventListFragment;
    private List<Event> events;

    final int EVENT_ITEM = 0;


    class EventItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView location;
        TextView time;
        View itemContainer;

        public EventItemViewHolder(View view) {
            super(view);
            this.itemContainer = view;
            this.title = (TextView)view.findViewById(R.id.team_item_team_name);
            this.time = (TextView)view.findViewById(R.id.team_item_team_description);
        }
    }


    public EventsAdapter(EventsActivity eventsActivity, EventListFragment eventListFragment) {
        this.events = new ArrayList<>();
        this.eventListFragment = eventListFragment;
        this.eventsActivity = eventsActivity;
    }


    public void setEvents(List<Event> events){
        this.events = events;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
            return EVENT_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType){
            case EVENT_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false);
                viewHolder = new EventItemViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case EVENT_ITEM:
                EventItemViewHolder eventItemViewHolder = (EventItemViewHolder)holder;
                final Event event = getEvent(position);
                eventItemViewHolder.title.setText(event.getName());
                eventItemViewHolder.time.setText(event.getStart().toString());
                eventItemViewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventListFragment.navigateToEventDetail(event);
                    }
                });
                break;
        }
    }

    protected Event getEvent(int position){
        return this.events.get(position);
    }


    @Override
    public int getItemCount() {
        return events.size();
    }
}