package com.mathandoro.coachplus.views.EventList;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.views.viewHolders.EventItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;



public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventListActivity eventListActivity;
    private final EventListFragment eventListFragment;
    private List<Event> visibleEvents;

    RecycleViewStack viewStack;

    final int EVENT_ITEM = 0;
    final int NO_EVENTS_ITEM = 1;
    final int FOOTER = 2;
    boolean futureEvents;

    public EventListAdapter(EventListActivity eventListActivity, EventListFragment eventListFragment, boolean futureEvents) {
        this.visibleEvents = new ArrayList<>();
        this.eventListFragment = eventListFragment;
        this.eventListActivity = eventListActivity;
        this.futureEvents = futureEvents;
        viewStack = new RecycleViewStack();
        viewStack.addSection(EVENT_ITEM, 0);
        viewStack.addSection(NO_EVENTS_ITEM, 0);
        viewStack.addSection(FOOTER, 1);

        this.subscribeData();
    }

    public void subscribeData(){
        AppState.instance().events$.subscribe(events -> {
            this.visibleEvents = filterVisibleEvents(events);
            viewStack.updateSection(EVENT_ITEM, visibleEvents.size());
            if(visibleEvents.size() == 0){
                viewStack.updateSection(NO_EVENTS_ITEM, 1);
            }
            else {
                viewStack.updateSection(NO_EVENTS_ITEM, 0);
            }
            this.notifyDataSetChanged();
        });
    }

    private List<Event> filterVisibleEvents(List<Event> events){
        if(futureEvents){
            return this.filterUpcomingEvents(events);
        }
        return this.filterPastEvents(events);
    }

    private List<Event> filterUpcomingEvents(List<Event> events){
        List<Event> upcomingEvents = new ArrayList<Event>();
        for(Event event: events){
            if(event.getEnd().after(new Date())){
                upcomingEvents.add(event);
            }
        }
        Collections.sort(upcomingEvents, (eventA, eventB) -> eventA.getStart().compareTo(eventB.getStart()));
        return upcomingEvents;
    }

    private List<Event> filterPastEvents(List<Event> events){
        List<Event> pastEvents = new ArrayList<Event>();
        for(Event event: events){
            if(event.getEnd().before(new Date())){
                pastEvents.add(event);
            }
        }
        Collections.sort(pastEvents, (eventA, eventB) -> eventA.getEnd().compareTo(eventB.getEnd()) * -1);
        return pastEvents;
    }

    @Override
    public int getItemViewType(int position) {
        return this.viewStack.getSectionIdAt(position);
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
            case NO_EVENTS_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_no_event_item, parent, false);
                TextView textView = view.findViewById(R.id.event_no_event_item_text);
                if(futureEvents){
                    textView.setText(R.string.No_Events_found);
                }
                else {
                    textView.setText(R.string.No_Events_found);
                }
                viewHolder = new StaticViewHolder(view);
                break;
            case FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
                viewHolder = new StaticViewHolder(view);
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
        return this.visibleEvents.get(viewStack.positionInSection(EVENT_ITEM, position));
    }

    @Override
    public int getItemCount() {
        return viewStack.size();
    }
}