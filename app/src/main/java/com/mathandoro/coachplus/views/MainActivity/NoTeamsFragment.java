package com.mathandoro.coachplus.views.MainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mathandoro.coachplus.R;


public class NoTeamsFragment extends Fragment {


    private NoTeamsFragmentListener listener;

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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_teams, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button createTeamButton = view.findViewById(R.id.createTeamButton);
        createTeamButton.setOnClickListener((View v) -> listener.onRegisterTeamButtonPressed());
    }



    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public interface NoTeamsFragmentListener {
        void onRegisterTeamButtonPressed();
    }
}
