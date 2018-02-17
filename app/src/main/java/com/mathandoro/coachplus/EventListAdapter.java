package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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


    class EventItemViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView location;
        TextView time;
        View itemContainer;

        public EventItemViewHolder(View view) {
            super(view);
            this.itemContainer = view;
            this.title = (TextView)view.findViewById(R.id.event_item_event_name);
            this.time = (TextView)view.findViewById(R.id.event_item_event_start);
            this.location = (TextView)view.findViewById(R.id.event_item_event_location);
        }
    }


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
                eventItemViewHolder.title.setText(event.getName());
                if(event.getStart() != null){
                    eventItemViewHolder.time.setText(this.formatGermanTimestamp(event.getStart()));
                }
                // todo location
                eventItemViewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventListFragment.navigateToEventDetail(event);
                    }
                });
                break;
        }
    }

    public String formatGermanTimestamp(Date indate)
    {
        String dateString = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy - HH:mm");
        try{
            dateString = simpleDateFormat.format( indate ) + " Uhr";
        }catch (Exception exception){
        }
        return dateString;
    }

    protected Event getEvent(int position){
        return this.events.get(position);
    }


    @Override
    public int getItemCount() {
        return events.size();
    }
}