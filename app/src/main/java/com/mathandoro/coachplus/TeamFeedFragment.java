package com.mathandoro.coachplus;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.data.DataLayer;
import com.mathandoro.coachplus.data.DataLayerCallback;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.Response.ApiResponse;
import com.mathandoro.coachplus.models.Response.InvitationResponse;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.models.TeamMember;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeamFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_TEAM = "MEMBERSHIP";
    private Settings settings;
    // private Team team;
    private Membership membership;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private TeamFeedAdapter teamFeedAdapter;
    protected DataLayer dataLayer;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected DataLayerCallback<List<TeamMember>> loadTeamMembersCallback = new DataLayerCallback<List<TeamMember>>() {
        @Override
        public void dataChanged(List<TeamMember> members) {
            if(members.size() > 0){
                for(int i =0;i < 15; i++){
                    members.add(members.get(0));
                }
            }
            teamFeedAdapter.setMembers(members);
            swipeRefreshLayout.setRefreshing(false);

        }

        @Override
        public void error() {
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    private FloatingActionButton inviteToTeamFab;
    private FloatingActionButton addEventFab;
    private FloatingActionsMenu floatingActionsMenu;

    public TeamFeedFragment() {
        // Required empty public constructor
    }

    public Membership getCurrentMembership(){
        return this.membership;
    }

    public static TeamFeedFragment newInstance(Membership membership) {
        TeamFeedFragment fragment = new TeamFeedFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEAM,  membership);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(this.getActivity());

        dataLayer = DataLayer.getInstance(this.getActivity());
        if (getArguments() != null) {
            membership = getArguments().getParcelable(ARG_TEAM);
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
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(membership.getTeam().getName());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.team_feed);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = recyclerView.getChildAt(0);
                if(view != null && recyclerView.getChildAdapterPosition(view) == 0){
                    View teamImageView = view.findViewById(R.id.team_feed_team_image);
                    teamImageView.setTranslationY(-view.getTop()/2);
                }
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        teamFeedAdapter = new TeamFeedAdapter((MainActivity)getActivity(), this);
        mRecyclerView.setAdapter(teamFeedAdapter);

        floatingActionsMenu  = (FloatingActionsMenu)view.findViewById(R.id.team_feed_floating_menu);

        addEventFab = (FloatingActionButton)view.findViewById(R.id.team_feed_add_event_fab);
        addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        inviteToTeamFab = (FloatingActionButton) view.findViewById(R.id.team_feed_invite_fab);
        inviteToTeamFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteToTeam();
            }
        });

        if(!membership.getRole().equals("coach")){
            if(!membership.getTeam().isPublic()){
                floatingActionsMenu.setVisibility(View.GONE);
            }
            else{
                addEventFab.setVisibility(View.GONE);
            }
        }
        dataLayer.getTeamMembers(membership.getTeam(), true, loadTeamMembersCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        dataLayer.getTeamMembers(membership.getTeam(), false, loadTeamMembersCallback);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void navigateToAllEvents() {
        Intent intent = new Intent(getActivity(), EventsActivity.class);
        intent.putExtra("team", membership.getTeam());
        startActivity(intent);
    }

    void closeActionMenu(){
        floatingActionsMenu.collapse();
    }

    void inviteToTeam(){
        final ProgressDialog dialog = ProgressDialog.show(this.getActivity(), "",
                "Creating invitation link...", true);
        dialog.show();

        Call<ApiResponse<InvitationResponse>> invitationUrl = ApiClient.instance().teamService.createInvitationUrl(settings.getToken(), membership.getTeam().get_id());
        invitationUrl.enqueue(new Callback<ApiResponse<InvitationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<InvitationResponse>> call, Response<ApiResponse<InvitationResponse>> response) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "hey, join " +  membership.getTeam().getName() +  " on coach+  "  + response.body().content.getUrl());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Invite new team members"));
                dialog.hide();
            }

            @Override
            public void onFailure(Call<ApiResponse<InvitationResponse>> call, Throwable t) {
                // todo show error (snackbar?)
                dialog.hide();
            }
        });

        closeActionMenu();

    }

    void createEvent(){
        closeActionMenu();
        Intent intent = new Intent(this.getActivity(), CreateEventActivity.class);
        intent.putExtra("team", membership.getTeam());
        startActivity(intent);
    }
}
