package com.mathandoro.coachplus.views.TeamView;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.Navigation;
import com.mathandoro.coachplus.persistence.AppState;
import com.mathandoro.coachplus.persistence.DataLayer;


public class NoTeamsFragment extends Fragment {


    private NoTeamsFragmentListener listener;
    private DataLayer dataLayer;

    public NoTeamsFragment() {
    }

    public static NoTeamsFragment newInstance(NoTeamsFragmentListener listener) {
        NoTeamsFragment fragment = new NoTeamsFragment();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(NoTeamsFragmentListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLayer = new DataLayer(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_teams, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.no_teams_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // reload memberships
            dataLayer.getMyMembershipsV2(false).subscribe(myMembershipsResponse -> {
               // todo set memberships in appState AppState.instance().
                if(myMembershipsResponse.getMemberships().size() > 0){
                    Navigation.navigateToMembership(this.getActivity(), myMembershipsResponse.getMemberships().get(0));
                    getActivity().finish();
                }
            });
            swipeRefreshLayout.setRefreshing(false);
        });

        Button createTeamButton = view.findViewById(R.id.no_teams_create_team_button);
        createTeamButton.setOnClickListener((View v) -> listener.onRegisterTeamButtonPressed());
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface NoTeamsFragmentListener {
        void onRegisterTeamButtonPressed();
    }
}
