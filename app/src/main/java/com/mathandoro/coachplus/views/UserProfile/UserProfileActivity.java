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
import com.mathandoro.coachplus.views.MainActivity.MainActivity;
import com.mathandoro.coachplus.views.MainActivity.TeamFeedAdapter;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    protected ToolbarFragment toolbarFragment;
    private RecyclerView recyclerView;
    private DataLayer dataLayer;

    private ReducedUser user;
    private List<Membership> memberships;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        user = getIntent().getExtras().getParcelable("user");
        dataLayer = DataLayer.getInstance(this);

        setContentView(R.layout.user_profile_activity);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle("");

        recyclerView = (RecyclerView) findViewById(R.id.user_profile_recycler_view);
        // parallax
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
        final UserProfileAdapter adapter = new UserProfileAdapter(this);
        adapter.setUser(user);
        recyclerView.setAdapter(adapter);

        // todo load user

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
}
