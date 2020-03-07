package com.mathandoro.coachplus.views.TeamView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Role;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.MyCustomTabsHelper;
import com.mathandoro.coachplus.helpers.PreloadLayoutManager;
import com.mathandoro.coachplus.helpers.SnackbarHelper;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.TeamMember;
import com.mathandoro.coachplus.views.EventDetail.AppInfoBottomSheet;
import com.mathandoro.coachplus.views.TeamRegistrationActivity;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.UserProfile.UserProfileActivity;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.LoginActivity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;


public class TeamViewActivity extends AppCompatActivity implements NoTeamsFragment.NoTeamsFragmentListener,
        ToolbarFragment.ToolbarFragmentListener, SwipeRefreshLayout.OnRefreshListener, AppInfoBottomSheet.IAppInfoBottomSheetListener {

    public static final String PARAM_MEMBERSHIP = "membership";
    private Settings settings;
    private MyMembershipsAdapter myMembershipsAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DataLayer dataLayer;
    private DrawerLayout drawer;
    protected ToolbarFragment toolbarFragment;
    protected List<Membership> memberships;
    protected MyReducedUser myUser;
    private SwipeRefreshLayout membershipsSwipeRefreshLayout;
    private TeamViewFragment teamViewFragment;
    private CustomTabsIntent customTabsIntent;

    public static int CREATE_TEAM_REQUEST = 1;
    public static final int EDIT_TEAM_REQUEST = 2;

    String TAG = "coach";
    private Membership currentMembership;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = new Settings(this);
        this.customTabsIntent = MyCustomTabsHelper.newIntent(this);

        setContentView(R.layout.team_view_activity);

        dataLayer = new DataLayer(TeamViewActivity.this);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showUserIcon();

        drawer = findViewById(R.id.drawer_layout);

        this.loadNavigationDrawer();
        this.loadMembershipsRecyclerView();
        this.loadMemberships(getIntent().getParcelableExtra(PARAM_MEMBERSHIP), true);
        this.loadMyUser();

        initFirebase();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        // todo check if authenticated
        // todo switch to team, open event
        // getIntent().getExtras().getString("eventId");
    }

    private void initFirebase(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String pushToken = task.getResult().getToken();
                    String deviceId = task.getResult().getId();
                    settings.setDeviceId(deviceId);

                    dataLayer.registerOrUpdateDevice(deviceId, pushToken).subscribe(data -> {
                        Log.d( "firebase", "firebase initialized");
                    }, error -> {
                        Log.d( "firebase", "firebase init error");
                    });
                });
    }

    private void loadNavigationDrawer(){
        ImageView registerTeamImage = findViewById(R.id.registerTeam);
        ImageView infoButton = findViewById(R.id.info);
        Button logoutView = findViewById(R.id.team_view_logout_button);

        registerTeamImage.setOnClickListener((View v) ->
                TeamViewActivity.this.navigateToCreateTeamActivity()
        );
        infoButton.setOnClickListener((View v) -> {
            AppInfoBottomSheet appInfoBottomSheet = new AppInfoBottomSheet(this);
            appInfoBottomSheet.show(getSupportFragmentManager(), appInfoBottomSheet.getTag());
        });
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

    private Observable<List<Membership>> loadMemberships(Membership joinedMembership, boolean switchTeam){
        PublishSubject subject = PublishSubject.create();
        this.dataLayer.getMyMembershipsV2(false).subscribe(myMembershipsResponse -> {
            membershipsSwipeRefreshLayout.setRefreshing(false);
            memberships = myMembershipsResponse.getMemberships();
            myMembershipsAdapter.setMemberships(myMembershipsResponse.getMemberships());
            Membership switchedMembership = null;
            if(!switchTeam){
                subject.onNext(memberships);
                subject.onComplete();
                return;
            }
            if(joinedMembership != null){
                for (Membership membership : memberships) {
                    if (membership.getTeam().get_id().equals(joinedMembership.getTeam().get_id())) {
                        switchedMembership = membership;
                    }
                }
            }
            String activeTeamId = settings.getActiveTeamId();
            if(switchedMembership != null){
                switchTeamContext(switchedMembership);
            }
            else if(memberships.size() > 0 && activeTeamId == null){
                switchTeamContext(memberships.get(0));
                return;
            }
            else {
                loadActiveTeam();
            }
            subject.onNext(memberships);
            subject.onComplete();
        });
        return subject;
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
        toolbarFragment.setTeamName(membership.getTeam().getName());
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
        this.reload();
    }

    public void reload(){
        Observable<List<Membership>> listObservable = this.loadMemberships(currentMembership, false);
        listObservable.subscribe(memberships1 -> {
            for (Membership membership : memberships1) {
                if(membership.getTeam().get_id().equals(settings.getActiveTeamId())){
                    teamViewFragment.onMembershipRefreshed(membership);
                    toolbarFragment.setTeamName(membership.getTeam().getName());
                }
            }
        });
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
                            // showNotification(getString(R.string., member.getUser().getFirstname()));
                            // todo
                        },
                        error -> showError());
                bottomSheet.dismiss();
            }

            @Override
            public void onChangeRole(String newRole) {
                dataLayer.updateRole(member.get_id(), newRole)
                        .subscribe(result -> {
                    teamViewFragment.reloadMembers().subscribe();
                    bottomSheet.dismiss();
                   //  String notificationText = getString(R.st, member.getUser().getFirstname());
                            String notificationText = "user is no longer a coach";
                    if(newRole.equals(Role.COACH)){
                        // notificationText = getString("user is now a coach", member.getUser().getFirstname());
                        notificationText = "user is now a coach";
                    }
                    showNotification(notificationText);
                }, error -> bottomSheet.dismiss());
            }
        });
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    private void showError(){
        Snackbar.make(teamViewFragment.getView(), getString(R.string.Error), Snackbar.LENGTH_SHORT).show();
    }

    private void showNotification(String text){
        Snackbar.make(teamViewFragment.getView(), text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShowThirdPartyLicenses() {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }

    @Override
    public void onShowImpressum() {
        CustomTabsHelper.openCustomTab(this, customTabsIntent,
                Uri.parse("https://coach.plus/impressum"),
                new WebViewFallback());
    }

    @Override
    public void onShowDataPrivacyStatement() {
        CustomTabsHelper.openCustomTab(this, customTabsIntent,
                Uri.parse("https://coach.plus/data-privacy"),
                new WebViewFallback());
    }

    @Override
    public void onShowTermsOfUse() {
        CustomTabsHelper.openCustomTab(this, customTabsIntent,
                Uri.parse("https://coach.plus/terms-of-use"),
                new WebViewFallback());
    }
}

