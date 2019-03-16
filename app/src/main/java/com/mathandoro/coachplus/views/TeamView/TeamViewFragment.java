package com.mathandoro.coachplus.views.TeamView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.ApiClient;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.InvitationResponse;
import com.mathandoro.coachplus.views.CreateEventActivity;
import com.mathandoro.coachplus.views.EventDetail.EventDetailActivity;
import com.mathandoro.coachplus.views.EventList.EventListActivity;
import com.mathandoro.coachplus.views.TeamRegistrationActivity;
import com.mathandoro.coachplus.views.UserProfile.UserProfileActivity;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeamViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_MEMBERSHIP = "MEMBERSHIP";
    private Settings settings;
    private Membership membership;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private TeamViewAdapter teamViewAdapter;
    protected DataLayer dataLayer;
    protected SwipeRefreshLayout swipeRefreshLayout;

    private FloatingActionButton inviteToTeamFab;
    private FloatingActionButton addEventFab;
    private FloatingActionButton editTeamFab;
    private FloatingActionsMenu floatingActionsMenu;

    public TeamViewFragment() {
        // Required empty public constructor
    }

    public Membership getCurrentMembership(){
        return this.membership;
    }

    public static TeamViewFragment newInstance(Membership membership) {
        TeamViewFragment fragment = new TeamViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MEMBERSHIP,  membership);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(this.getActivity());

        dataLayer = DataLayer.getInstance(this.getActivity());
        if (getArguments() != null) {
            membership = getArguments().getParcelable(ARG_MEMBERSHIP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.team_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.team_feed);
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
        teamViewAdapter = new TeamViewAdapter((TeamViewActivity)getActivity(), this, membership);
        mRecyclerView.setAdapter(teamViewAdapter);

        floatingActionsMenu  = view.findViewById(R.id.team_feed_floating_menu);

        addEventFab = view.findViewById(R.id.team_feed_add_event_fab);
        addEventFab.setOnClickListener((View v) -> createEvent());

        editTeamFab = view.findViewById(R.id.team_feed_edit_team_fab);
        editTeamFab.setOnClickListener((View v) -> editTeam());

        inviteToTeamFab = view.findViewById(R.id.team_feed_invite_fab);
        inviteToTeamFab.setOnClickListener((View v) -> inviteToTeam());

        if(!membership.getRole().equals(Role.COACH)){
            addEventFab.setVisibility(View.GONE);
            editTeamFab.setVisibility(View.GONE);

            if(!membership.getTeam().isPublic()){
                inviteToTeamFab.setVisibility(View.GONE);
                floatingActionsMenu.setVisibility(View.GONE);
            }
        }

        loadData();
    }

    private void loadData(){
        boolean useCache = false;
        // todo reload team image / membership
        Observable<TeamMembersResponse> teamMembersV2 = dataLayer.getTeamMembersV2(membership.getTeam(), useCache);
        Observable<EventsResponse> eventsV2 = dataLayer.getEvents(membership.getTeam(), useCache);

        Observable.zip(teamMembersV2, eventsV2, (teamMembersResponse, eventsResponse) -> {
            teamViewAdapter.setMembers(teamMembersResponse.getMembers());
            teamViewAdapter.setUpcomingEvents(eventsResponse.getEvents());
            swipeRefreshLayout.setRefreshing(false);
            return true;
        }).subscribe();
    }

    public void reloadMembers(){
        dataLayer.getTeamMembersV2(membership.getTeam(), false)
                .subscribe(teamMembersResponse -> teamViewAdapter.setMembers(teamMembersResponse.getMembers()),
                        error -> {});
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    public void navigateToEvent(Event event) {
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventDetailActivity.EXTRA_EVENT, event);
        bundle.putParcelable(EventDetailActivity.EXTRA_TEAM, membership.getTeam());
        bundle.putParcelable(EventDetailActivity.EXTRA_MEMBERSHIP, membership);
        intent.putExtra(EventDetailActivity.EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void navigateToAllEvents() {
        Intent intent = new Intent(getActivity(), EventListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventListActivity.EXTRA_TEAM, membership.getTeam());
        bundle.putParcelable(EventListActivity.EXTRA_MEMBERSHIP, membership);
        intent.putExtra(EventListActivity.EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }

    public void navigateToUserProfile(ReducedUser user) {
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.INTENT_PARAM_USER, user);
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

    void editTeam(){
        closeActionMenu();
        Intent intent = new Intent(this.getActivity(), TeamRegistrationActivity.class);
        intent.putExtra(TeamRegistrationActivity.INTENT_PARAM_TEAM, membership.getTeam());
        startActivity(intent);
    }
}
