package com.mathandoro.coachplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    protected ToolbarFragment toolbarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.user_profile_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }
}
