package com.mathandoro.coachplus.views.EventList;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.views.viewHolders.EventItemViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;

import java.util.ArrayList;
import java.util.List;



public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final EventListActivity eventListActivity;
    private final EventListFragment eventListFragment;
    private List<Event> events;

    RecycleViewStack viewStack;

    final int EVENT_ITEM = 0;
    final int NO_EVENTS_ITEM = 1;
    final int FOOTER = 2;

    public EventListAdapter(EventListActivity eventListActivity, EventListFragment eventListFragment) {
        this.events = new ArrayList<>();
        this.eventListFragment = eventListFragment;
        this.eventListActivity = eventListActivity;
        viewStack = new RecycleViewStack();
        viewStack.addSection(EVENT_ITEM, 0);
        viewStack.addSection(NO_EVENTS_ITEM, 0);
        viewStack.addSection(FOOTER, 1);
    }

    public void setEvents(List<Event> events){
        this.events = events;
        viewStack.updateSection(EVENT_ITEM, events.size());
        if(events.size() == 0){
            viewStack.updateSection(NO_EVENTS_ITEM, 1);
        }
        else {
            viewStack.updateSection(NO_EVENTS_ITEM, 0);
        }
        this.notifyDataSetChanged();
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
        return this.events.get(viewStack.positionInSection(EVENT_ITEM, position));
    }

    @Override
    public int getItemCount() {
        return viewStack.size();
    }
}