package com.mathandoro.coachplus.views.UserProfile;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mathandoro.coachplus.BuildConfig;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.Settings;
import com.mathandoro.coachplus.api.Response.MyUserResponse;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.models.Team;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.views.TeamView.TeamViewActivity;
import com.mathandoro.coachplus.views.WebViewActivity;
import com.mathandoro.coachplus.views.layout.ImagePickerView;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.views.viewHolders.MembershipViewHolder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import io.reactivex.Observable;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener, ImagePickerView.ImagePickerListener, MembershipViewHolder.MembershipViewHolderListener {

    public final static String INTENT_PARAM_USER = "user";

    protected ToolbarFragment toolbarFragment;
    private RecyclerView recyclerView;
    private DataLayer dataLayer;

    private ReducedUser user;
    private boolean isMyUser = false;
    private UserProfileAdapter adapter;
    private Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        this.settings = new Settings(this);

        dataLayer = DataLayer.getInstance(this);

        recyclerView = findViewById(R.id.user_profile_recycler_view);
        adapter = new UserProfileAdapter(this, dataLayer);
        recyclerView.setAdapter(adapter);


        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle("");




        // parallax scroll effect
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = recyclerView.getChildAt(0);
                if(view != null && recyclerView.getChildAdapterPosition(view) == 0){
                    View userImageView = view.findViewById(R.id.user_profile_image);
                    userImageView.setTranslationY(-view.getTop()/2);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        this.loadUser();
    }

    private void loadUser(){
        Observable<MyUserResponse> myUserV2 = dataLayer.getMyUserV2(true);
        myUserV2.subscribe(response -> {
            this.user = response.user;

            ReducedUser userParam = getIntent().getExtras().getParcelable(INTENT_PARAM_USER);
            if(userParam != null){
                if(userParam.get_id().equals(this.user.get_id()))   {
                    isMyUser = true;
                }
                else{
                    this.user = userParam;
                }
            }else{
                isMyUser = true;
            }
            adapter.setUser(user, isMyUser);
            if(isMyUser){
                toolbarFragment.showSettings();
            }
            loadMemberships();
        });
    }

    private void loadMemberships(){

        dataLayer.getMembershipsOfUser(user.get_id(), new DataLayerCallback<List<Membership>>() {
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
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL, BuildConfig.BASE_URL);
            intent.putExtra(WebViewActivity.TITLE, getString(R.string.user_profile_settings));

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

    // todo confirmation dialog

    public void navigateToMembership(Membership membership) {
        Intent intent = new Intent(this, TeamViewActivity.class);
        intent.putExtra(TeamViewActivity.PARAM_MEMBERSHIP, membership);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void joinTeam(Team team) {
        dataLayer.joinPublicTeam(team.get_id()).subscribe(membership -> {
            this.loadMemberships();
        }, error -> {});
    }

    @Override
    public void leaveTeam(Team team) {
        dataLayer.leaveTeam(team.get_id()).subscribe(membership -> {
            if(team.get_id().equals(settings.getActiveTeamId())){
               settings.setActiveTeamId(null);
               this.navigateToMembership(null);
            }else {
                this.loadMemberships();
            }
        }, error -> {});
    }
}
