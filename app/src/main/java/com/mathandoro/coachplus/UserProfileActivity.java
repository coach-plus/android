package com.mathandoro.coachplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mathandoro.coachplus.R;

public class UserProfileActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {

    protected ToolbarFragment toolbarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();

    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }
}
