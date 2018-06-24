package com.mathandoro.coachplus.views.UserProfile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.models.ReducedUser;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.persistence.DataLayerCallback;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    public final static String INTENT_PARAM_IS_ME = "isMe";
    public final static String INTENT_PARAM_USER = "user";

    protected ToolbarFragment toolbarFragment;
    private RecyclerView recyclerView;
    private DataLayer dataLayer;

    private ReducedUser user;
    private boolean isMyUser;
    private List<Membership> memberships;
    private UserProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLayer = DataLayer.getInstance(this);

        setContentView(R.layout.user_profile_activity);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle("");

        recyclerView = findViewById(R.id.user_profile_recycler_view);

        // parallax scroll effect
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = recyclerView.getChildAt(0);
                if(view != null && recyclerView.getChildAdapterPosition(view) == 0){
                    View userImageView = view.findViewById(R.id.user_profile_user_image);
                    userImageView.setTranslationY(-view.getTop()/2);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        this.loadUser();
    }

    private void loadUser(){
        isMyUser = getIntent().getExtras().getBoolean(INTENT_PARAM_IS_ME, true);
        if(!isMyUser){
            user = getIntent().getExtras().getParcelable(INTENT_PARAM_USER);
            loadMemberships();
        }
        else {
            dataLayer.getMyUser(true, response -> {
                user = response.user;
                loadMemberships();
            });
        }
    }

    private void loadMemberships(){
        adapter = new UserProfileAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setUser(user);
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
    }
}
