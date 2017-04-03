package com.mathandoro.coachplus;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.data.DataLayerCallback;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.List;


public class TeamFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_TEAM = "TEAM";
    private Team team;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private TeamFeedAdapter teamFeedAdapter;
    protected DataLayer dataLayer;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected DataLayerCallback<List<TeamMember>> loadTeamMembersCallback = new DataLayerCallback<List<TeamMember>>() {
        @Override
        public void dataChanged(List<TeamMember> members) {
            teamFeedAdapter.setMembers(members);
            swipeRefreshLayout.setRefreshing(false);

        }

        @Override
        public void error() {
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    public TeamFeedFragment() {
        // Required empty public constructor
    }

    public static TeamFeedFragment newInstance(Team team) {
        TeamFeedFragment fragment = new TeamFeedFragment();
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
        return inflater.inflate(R.layout.fragment_team_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(team.getName());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.team_feed);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        teamFeedAdapter = new TeamFeedAdapter((MainActivity)getActivity());
        mRecyclerView.setAdapter(teamFeedAdapter);

        // load data
        dataLayer.getTeamMembers(team, true, loadTeamMembersCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        dataLayer.getTeamMembers(team, false, loadTeamMembersCallback);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}