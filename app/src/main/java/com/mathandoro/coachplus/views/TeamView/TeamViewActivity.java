package com.mathandoro.coachplus.views.TeamView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.PreloadLayoutManager;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.JWTUser;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.EventDetail.EventDetailActivity;
import com.mathandoro.coachplus.views.SplashScreenActivity;
import com.mathandoro.coachplus.views.TeamRegistrationActivity;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.UserProfile.UserProfileActivity;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.LoginActivity;

import java.util.List;

import io.reactivex.Observable;


public class TeamViewActivity extends AppCompatActivity implements NoTeamsFragment.NoTeamsFragmentListener,
        ToolbarFragment.ToolbarFragmentListener, SwipeRefreshLayout.OnRefreshListener {


    public static final String PARAM_MEMBERSHIP = "membership";
    private Settings settings;
    private MyMembershipsAdapter myMembershipsAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DataLayer dataLayer;
    private DrawerLayout drawer;
    protected ToolbarFragment toolbarFragment;
    protected List<Membership> memberships;
    protected JWTUser myUser;
    private SwipeRefreshLayout membershipsSwipeRefreshLayout;
    private TeamViewFragment teamViewFragment;

    public static int CREATE_TEAM_REQUEST = 1;
    public static final int EDIT_TEAM_REQUEST = 2;


    String TAG = "coach";
    private Membership currentMembership;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(this);
        setContentView(R.layout.team_view_activity);

        dataLayer = DataLayer.getInstance(this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showUserIcon();

        drawer = findViewById(R.id.drawer_layout);

        this.loadNavigationDrawer();
        this.loadMembershipsRecyclerView();
        this.loadMemberships(getIntent().getParcelableExtra(PARAM_MEMBERSHIP), true);
        this.loadMyUser();

        // todo initFirebase();

    }

    private void initFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    //    String deviceId = task.getResult().getId(); ?correct ?

                    // Log and toast
                    // todo use String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, token);
                    Toast.makeText(TeamViewActivity.this, token, Toast.LENGTH_SHORT).show();
                });
    }

    private void loadNavigationDrawer(){
        ImageView registerTeamImage = findViewById(R.id.registerTeam);
        Button logoutView = findViewById(R.id.team_view_logout_button);
        registerTeamImage.setOnClickListener((View v) ->
                TeamViewActivity.this.navigateToCreateTeamActivity()
        );
        logoutView.setOnClickListener((View v) -> logout());
    }

    private void loadMyUser(){
        Observable<MyUserResponse> myUserV2 = this.dataLayer.getMyUserV2(true);
        myUserV2.subscribe((response) -> myUser = response.user);
    }

    private void logout(){
        settings.reset();
        // todo clear cached files
        Intent logoutIntent = new Intent(this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
    }

    private void loadMemberships(Membership joinedMembership, boolean switchTeam){
        this.dataLayer.getMyMembershipsV2(false).subscribe(myMembershipsResponse -> {
            membershipsSwipeRefreshLayout.setRefreshing(false);
            memberships = myMembershipsResponse.getMemberships();
            Membership updatedMembership = null;
            if(!switchTeam){
                return;
            }
            if(joinedMembership != null){
                for (Membership membership : memberships) {
                    if (membership.getTeam().get_id().equals(joinedMembership.getTeam().get_id())) {
                        updatedMembership = membership;
                    }
                }
            }
            myMembershipsAdapter.setMemberships(myMembershipsResponse.getMemberships());
            String activeTeamId = settings.getActiveTeamId();
            if(updatedMembership != null){
                switchTeamContext(updatedMembership);
            }
            else if(memberships.size() > 0 && activeTeamId == null){
                switchTeamContext(memberships.get(0));
                return;
            }
            else {
                loadActiveTeam();
            }
        });
    }

    private void loadActiveTeam(){
        String activeTeamId = this.settings.getActiveTeamId();
        if(activeTeamId != null) {
            for (Membership m : memberships) {
                if (m.getTeam().get_id().equals(activeTeamId)){
                    this.switchTeamContext(m);
                    return;
                }
            }
        }
        NoTeamsFragment noTeamsFragment = NoTeamsFragment.newInstance(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, noTeamsFragment)
                .commit();
    }

    public void navigateToCreateTeamActivity(){
        Intent createTeamIntent = new Intent(this, TeamRegistrationActivity.class);
        startActivityForResult(createTeamIntent, CREATE_TEAM_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_TEAM_REQUEST && resultCode == RESULT_OK){
            Membership newMembership = data.getParcelableExtra(TeamRegistrationActivity.RETURN_PARAM_MEMBERSHIP);
            this.memberships.add(newMembership);
            myMembershipsAdapter.setMemberships(memberships);
            switchTeamContext(newMembership);
        }
        else if(requestCode == EDIT_TEAM_REQUEST && resultCode == RESULT_OK){
            loadMemberships(currentMembership, true);
        }
    }

    private void loadMembershipsRecyclerView(){
        mRecyclerView = findViewById(R.id.memberships_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        membershipsSwipeRefreshLayout = findViewById(R.id.memberships_swipe_layout);
        membershipsSwipeRefreshLayout.setOnRefreshListener(this);

        mLinearLayoutManager = new PreloadLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        myMembershipsAdapter = new MyMembershipsAdapter(this);
        mRecyclerView.setAdapter(myMembershipsAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void switchTeamContext(Membership membership) {
        reloadMembershipsView();
        currentMembership = membership;
        toolbarFragment.setTeam(membership.getTeam());
        this.settings.setActiveTeamId(membership.getTeam().get_id());
        teamViewFragment = TeamViewFragment.newInstance(membership);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_activity_fragment_container, teamViewFragment)
                .commit();

        drawer.closeDrawer(GravityCompat.START);
    }

    private void reloadMembershipsView(){
        // reload by reapplying adapter
        mRecyclerView.setAdapter(myMembershipsAdapter);
    }

    @Override
    public void onRegisterTeamButtonPressed() {
        drawer.closeDrawer(GravityCompat.START);
        this.navigateToCreateTeamActivity();
    }

    @Override
    public void onLeftIconPressed() {
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onRightIconPressed() {
        this.navigateToMyUserProfile();
    }

    public void navigateToMyUserProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user", myUser);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        this.loadMemberships(currentMembership, false);
    }

    public void showBottomSheet(TeamMember member){

        String newRole = member.getRole().equals(Role.COACH) ? Role.USER : Role.COACH;
        TeamViewBottomSheet bottomSheet = TeamViewBottomSheet.newInstance(newRole);

        bottomSheet.setListener(new TeamViewBottomSheet.ITeamViewBottomSheetEvent() {
            @Override
            public void onKickUser() {
                dataLayer.removeUserFromTeam(member.get_id()).subscribe(
                        (response) -> {
                            teamViewFragment.reloadMembers();
                            showNotification(getString(R.string.user_removed_sucessfully, member.getUser().getFirstname()));
                        },
                        error -> showError());
                bottomSheet.dismiss();
            }

            @Override
            public void onChangeRole(String newRole) {
                dataLayer.updateRole(member.get_id(), newRole)
                        .subscribe(result -> {
                    teamViewFragment.reloadMembers();
                    bottomSheet.dismiss();
                    String notificationText = getString(R.string.user_is_no_longer_coach, member.getUser().getFirstname());
                    if(newRole.equals(Role.COACH)){
                        notificationText = getString(R.string.user_is_now_coach, member.getUser().getFirstname());
                    }
                    showNotification(notificationText);
                }, error -> bottomSheet.dismiss());
            }
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    private void showError(){
        Snackbar.make(teamViewFragment.getView(), getString(R.string.error_occurred), Snackbar.LENGTH_SHORT).show();
    }

    private void showNotification(String text){
        Snackbar.make(teamViewFragment.getView(), text, Snackbar.LENGTH_SHORT).show();
    }
}

