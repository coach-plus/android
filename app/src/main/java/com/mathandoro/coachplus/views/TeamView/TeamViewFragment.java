package com.mathandoro.coachplus.views.TeamView;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.mathandoro.coachplus.api.ApiError;
import com.mathandoro.coachplus.api.ApiErrorUtils;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.MembershipResponse;
import com.mathandoro.coachplus.api.Response.MyMembershipsResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.persistence.AppState;
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

import static com.mathandoro.coachplus.views.TeamView.TeamViewActivity.EDIT_TEAM_REQUEST;


public class TeamViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_MEMBERSHIP = "MEMBERSHIP";
    private static final int REQUEST_SHOW_EVENT_DETAILS = 1;
    private static final int REQUEST_SHOW_ALL_EVENTS = 2;
    private static final int REQUEST_SHOW_USER_PROFILE = 3;
    private static final int REQUEST_CREATE_EVENT = 4;


    private Settings settings;
    private Membership membership;
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

    TeamViewActivity teamViewActivity;

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
        teamViewActivity = (TeamViewActivity) getActivity();
        this.settings = new Settings(this.getActivity());

        AppState.instance().myUserChanged$.subscribe(user -> reloadMembers().subscribe());

        dataLayer = new DataLayer(this.getActivity());
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

        applyRole();

        loadData().subscribe();
    }

    private void applyRole(){
        if(!membership.getRole().equals(Role.COACH)){
            addEventFab.setVisibility(View.GONE);
            editTeamFab.setVisibility(View.GONE);

            if(!membership.getTeam().isPublic()){
                inviteToTeamFab.setVisibility(View.GONE);
                floatingActionsMenu.setVisibility(View.GONE);
            }
        }
        else {
            addEventFab.setVisibility(View.VISIBLE);
            editTeamFab.setVisibility(View.VISIBLE);
            inviteToTeamFab.setVisibility(View.VISIBLE);
            floatingActionsMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        reloadEvents().subscribe();
        reloadMembers().subscribe();
    }

    private Observable loadData(){
        boolean useCache = false;

        Observable<TeamMembersResponse> teamMembersV2 = dataLayer.getTeamMembersV2(membership.getTeam(), useCache);
        Observable<EventsResponse> eventsV2 = dataLayer.getEvents(membership.getTeam(), useCache);
        Observable<MembershipResponse> updatedMembership = dataLayer.getMyMembership(membership.getId(), useCache);

        return Observable.zip(teamMembersV2, eventsV2, updatedMembership, (teamMembersResponse, eventsResponse, updatedMembershipResponse) -> {
            AppState.instance().setEvents(eventsResponse.getEvents());
            AppState.instance().setMembers(teamMembersResponse.getMembers());
            this.membership = updatedMembershipResponse.getMembership();
            applyRole();
            // teamViewAdapter.setMembers(teamMembersResponse.getMembers());
            // teamViewAdapter.setUpcomingEvents(eventsResponse.getEvents());
            swipeRefreshLayout.setRefreshing(false);
            return true;
        });
    }

    public Observable<TeamMembersResponse> reloadMembers(){
        return dataLayer.getTeamMembersV2(membership.getTeam(), false)
                .map(teamMembersResponse -> {
                    AppState.instance().setMembers(teamMembersResponse.getMembers());
                    return teamMembersResponse;
                });
    }

    public Observable<EventsResponse> reloadEvents(){
        return dataLayer.getEvents(membership.getTeam(), false)
                .map(response -> {
                    AppState.instance().setEvents(response.getEvents());
                    return response;
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        reload();
    }

    private void reload(){
        teamViewActivity.reload();
        this.loadData().subscribe( c -> {
            applyRole();
        });
    }

    public void onMembershipRefreshed(Membership updatedMembership) {
        teamViewAdapter.setMembership(updatedMembership);
        applyRole();
        Observable.zip(reloadEvents(), reloadMembers(), (events, members) -> {
            swipeRefreshLayout.setRefreshing(false);
            return true;
        }).subscribe();
    }

    public void navigateToAllEvents() {
        Intent intent = new Intent(getActivity(), EventListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventListActivity.EXTRA_TEAM, membership.getTeam());
        bundle.putParcelable(EventListActivity.EXTRA_MEMBERSHIP, membership);
        intent.putExtra(EventListActivity.EXTRA_BUNDLE, bundle);
        startActivityForResult(intent, REQUEST_SHOW_ALL_EVENTS);
    }

    public void navigateToEvent(Event event) {
        Intent intent = new Intent(getContext(), EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EventDetailActivity.EXTRA_EVENT, event);
        bundle.putParcelable(EventDetailActivity.EXTRA_TEAM, membership.getTeam());
        bundle.putParcelable(EventDetailActivity.EXTRA_MEMBERSHIP, membership);
        intent.putExtra(EventDetailActivity.EXTRA_BUNDLE, bundle);
        startActivityForResult(intent, REQUEST_SHOW_EVENT_DETAILS);
    }

    public void navigateToUserProfile(ReducedUser user) {
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.INTENT_PARAM_USER, user);
        startActivityForResult(intent, REQUEST_SHOW_USER_PROFILE);
    }

    void closeActionMenu(){
        floatingActionsMenu.collapse();
    }

    void inviteToTeam(){
        final ProgressDialog dialog = ProgressDialog.show(this.getActivity(), "",
                "...", true);
        dialog.show();

        Call<ApiResponse<InvitationResponse>> invitationUrl = ApiClient.instance().teamService.createInvitationUrl(settings.getToken(), membership.getTeam().get_id());
        invitationUrl.enqueue(new Callback<ApiResponse<InvitationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<InvitationResponse>> call, Response<ApiResponse<InvitationResponse>> response) {
                if(response.isSuccessful()){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    String invitationText = getResources().getString(R.string.You_are_invited_to_join_team,  membership.getTeam().getName(), response.body().content.getUrl());
                    sendIntent.putExtra(Intent.EXTRA_TEXT, invitationText);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent,  getResources().getString(R.string.Invite_to_team)));
                    dialog.hide();
                }
                else {
                    ApiError apiError = ApiErrorUtils.parseErrorResponse(response);
                    SnackbarHelper.showError(mRecyclerView, apiError.getMessage());
                }
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
        startActivityForResult(intent, REQUEST_CREATE_EVENT);
    }

    void editTeam(){
        closeActionMenu();
        Intent intent = new Intent(this.getActivity(), TeamRegistrationActivity.class);
        intent.putExtra(TeamRegistrationActivity.INTENT_PARAM_TEAM, membership.getTeam());
        getActivity().startActivityForResult(intent, EDIT_TEAM_REQUEST);
    }
}
