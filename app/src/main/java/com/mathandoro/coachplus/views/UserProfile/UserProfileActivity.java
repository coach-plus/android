package com.mathandoro.coachplus.views.UserProfile;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.helpers.Navigation;
import com.mathandoro.coachplus.models.MyReducedUser;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.views.UserSettingsActivity;
import com.mathandoro.coachplus.views.layout.ImagePickerView;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.viewHolders.MembershipViewHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener, ImagePickerView.ImagePickerListener, MembershipViewHolder.MembershipViewHolderListener {

    public final static String INTENT_PARAM_USER = "visibleUser";

    private ToolbarFragment toolbarFragment;
    private RecyclerView recyclerView;
    private DataLayer dataLayer;
    private ReducedUser visibleUser;
    private boolean isMyUser = false;
    private UserProfileAdapter adapter;
    private Settings settings;
    private ReducedUser userParam;
    private MyReducedUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        AppState.myUserChanged$.subscribe(user -> this.loadMyUser());

        this.settings = new Settings(this);
        dataLayer = DataLayer.getInstance(this);

        userParam = getIntent().getExtras().getParcelable(INTENT_PARAM_USER);

        recyclerView = findViewById(R.id.user_profile_recycler_view);
        adapter = new UserProfileAdapter(this, dataLayer);
        recyclerView.setAdapter(adapter);



        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle("");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        this.loadUser();
    }

    private void loadUser(){
        this.loadMyUser().subscribe(response -> loadMemberships());
    }

    private Observable<MyUserResponse> loadMyUser(){
        Observable<MyUserResponse> myUserV2 = dataLayer.getMyUserV2(true);
        PublishSubject<MyUserResponse> subject = PublishSubject.create();
        myUserV2.subscribe(response -> {
            myUser = response.user;

            if(userParam != null){
                if(userParam.get_id().equals(myUser.get_id()))   {
                    isMyUser = true;
                    visibleUser = myUser;
                }
                else{
                    visibleUser = userParam;
                }
            }else{
                visibleUser = myUser;
                isMyUser = true;
            }
            adapter.setUser(visibleUser, isMyUser);
            if(isMyUser){
                toolbarFragment.showSettings();
            }
            subject.onNext(response);
            subject.onComplete();
        });
        return subject;
    }



    private void loadMemberships(){

        dataLayer.getMembershipsOfUser(visibleUser.get_id(), new DataLayerCallback<List<Membership>>() {
            @Override
            public void dataChanged(List<Membership> memberships) {
                adapter.setMemberships(memberships);
            }

            @Override
            public void error() {

            }
        });
    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() {
        if(isMyUser){
            Intent intent = new Intent(this, UserSettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPickImage() {
        ImagePickerView.startImagePickerIntent(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
           adapter.handleImagePickerActivityResult(resultCode, data);
        }
    }

    public void navigateToMembership(Membership membership) {
        Navigation.navigateToMembership(this, membership);
    }

    @Override
    public void joinTeam(Team team) {
        final ConfirmationBottomSheet bottomSheet = ConfirmationBottomSheet.show(getSupportFragmentManager(), getString(R.string.join_team_confirmation, team.getName()), false);
        bottomSheet.setListener(new ConfirmationBottomSheet.IComfirmationBottomSheetListener() {
            @Override
            public void onConfirm() {
                bottomSheet.dismiss();
                dataLayer.joinPublicTeam(team.get_id()).subscribe(membership -> {
                    UserProfileActivity.this.loadMemberships();
                    bottomSheet.dismiss();
                }, error -> {
                });
            }

            @Override
            public void onDecline() {
                bottomSheet.dismiss();
            }
        });
    }

    @Override
    public void leaveTeam(Team team) {
        ConfirmationBottomSheet bottomSheet = ConfirmationBottomSheet.show(getSupportFragmentManager(),
                getString(R.string.leave_team_confirmation, team.getName()), true);
        bottomSheet.setListener( new ConfirmationBottomSheet.IComfirmationBottomSheetListener() {
            @Override
            public void onConfirm() {
                bottomSheet.dismiss();
                dataLayer.leaveTeam(team.get_id()).subscribe(membership -> {
                    if(team.get_id().equals(settings.getActiveTeamId())){
                        UserProfileActivity.this.navigateToMembership(null);
                    }else {
                        UserProfileActivity.this.loadMemberships();
                    }
                }, error -> {});
            }

            @Override
            public void onDecline() {
                bottomSheet.dismiss();
            }
        });
    }

}
