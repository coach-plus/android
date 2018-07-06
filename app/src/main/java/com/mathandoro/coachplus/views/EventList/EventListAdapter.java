package com.mathandoro.coachplus.views.EventList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventListActivity eventListActivity;
    private final EventListFragment eventListFragment;
    private List<Event> events;

    final int EVENT_ITEM = 0;


    public EventListAdapter(EventListActivity eventListActivity, EventListFragment eventListFragment) {
        this.events = new ArrayList<>();
        this.eventListFragment = eventListFragment;
        this.eventListActivity = eventListActivity;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
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
                eventItemViewHolder.bind(event);
                eventItemViewHolder.itemContainer.setOnClickListener((View v) ->
                    eventListFragment.navigateToEventDetail(event));
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