package com.mathandoro.coachplus.views.EventList;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.CreateEventActivity;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.views.layout.ToolbarFragment;
import com.mathandoro.coachplus.models.Team;

public class EventListActivity extends AppCompatActivity implements ToolbarFragment.ToolbarFragmentListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Team team;
    private Membership membership;
    private ToolbarFragment toolbarFragment;
    public static final String EXTRA_BUNDLE = "bundle";
    public static final String EXTRA_TEAM = "team";
    public static final String EXTRA_MEMBERSHIP = "membership";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);

        Bundle bundle = getIntent().getExtras().getBundle(EXTRA_BUNDLE);
        team = bundle.getParcelable(EXTRA_TEAM);
        membership = bundle.getParcelable(EXTRA_MEMBERSHIP);

        toolbarFragment = (ToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_toolbar);
        toolbarFragment.setListener(this);
        toolbarFragment.showBackButton();
        toolbarFragment.setTitle(getString(R.string.events_of)+" "+ this.team.getName());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton createEventFab = findViewById(R.id.create_event_fab);
        if(membership.isCoach()){
            createEventFab.setOnClickListener((View view) -> {

                Intent intent = new Intent(EventListActivity.this, CreateEventActivity.class);
                intent.putExtra(CreateEventActivity.INTENT_PARAM_TEAM, team);
                startActivity(intent);
            });
        }
        else{
            createEventFab.hide();
        }

    }

    @Override
    public void onLeftIconPressed() {
        finish();
    }

    @Override
    public void onRightIconPressed() { }


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_events, container, false);
            TextView textView = (rootView.findViewById(R.id.section_label));
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            boolean showUpcomingEvents = true;
            if(position == 1){
                showUpcomingEvents = false;
            }
            return EventListFragment.newInstance(team, membership, showUpcomingEvents);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.upcoming);
                case 1:
                    return getString(R.string.past);
            }
            return null;
        }
    }
}
