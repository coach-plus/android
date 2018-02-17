package com.mathandoro.coachplus;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.data.DataLayerCallback;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EventListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_TEAM = "TEAM";
    private Team team;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EventListAdapter eventListAdapter;
    protected DataLayer dataLayer;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected boolean showUpcomingEvents = true;

    protected  DataLayerCallback<List<Event>> loadEventsCallback = new DataLayerCallback<List<Event>>() {
        @Override
        public void dataChanged(List<Event> events) {
            List<Event> visibleEvents = this.filterVisibleEvents(events);
            eventListAdapter.setEvents(visibleEvents);
            swipeRefreshLayout.setRefreshing(false);
        }

        private List<Event> filterVisibleEvents(List<Event> events){
            if(showUpcomingEvents){
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
            return upcomingEvents;
        }

        private List<Event> filterPastEvents(List<Event> events){
            List<Event> pastEvents = new ArrayList<Event>();
            for(Event event: events){
                if(event.getEnd().before(new Date())){
                    pastEvents.add(event);
                }
            }
            return pastEvents;
        }

        @Override
        public void error() {
            swipeRefreshLayout.setRefreshing(false);
        }
    };



    public EventListFragment() {
        // Required empty public constructor
    }

    public void setShowUpcomingEvents(boolean showUpcomingEvents){
        this.showUpcomingEvents = showUpcomingEvents;
    }

    public static EventListFragment newInstance(Team team, boolean showUpcomingEvents) {
        EventListFragment fragment = new EventListFragment();
        fragment.setShowUpcomingEvents(showUpcomingEvents);
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEAM,  team);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLayer = DataLayer.getInstance(this.getActivity());
        if (getArguments() != null) {
            team = getArguments().getParcelable(ARG_TEAM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(team.getName());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.eventListSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.eventListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        eventListAdapter = new EventListAdapter((EventListActivity)getActivity(), this);
        mRecyclerView.setAdapter(eventListAdapter);

        // load data
        dataLayer.getEvents(team, true, loadEventsCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        dataLayer.getEvents(team, false, loadEventsCallback);
    }

    void navigateToEventDetail(Event event){
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra("team", team);
        intent.putExtra("event", event);
        startActivity(intent);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
