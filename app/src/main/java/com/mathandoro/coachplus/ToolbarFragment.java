package com.mathandoro.coachplus;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Team;
import com.squareup.picasso.Picasso;


public class ToolbarFragment extends Fragment {
    private Toolbar toolbar;
    private ImageView leftIcon;

    private ToolbarFragmentListener toolbarFragmentListener;

    public ToolbarFragment() {
        // Required empty public constructor
    }

    public void showMenuButton(){
        this.leftIcon.setImageResource(R.drawable.ic_dehaze_white_24dp);
    }

    public void showBackButton(){
        this.leftIcon.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    }

    public void setTeam(Team team){
        if(team.getImage() == null){
            leftIcon.setImageResource(R.drawable.ic_dehaze_white_24dp);
            return;
        }
        Picasso.with(leftIcon.getContext())
                .load(BuildConfig.BASE_URL+ "/uploads/" + team.getImage())
                .placeholder(R.drawable.ic_dehaze_white_24dp)
                .transform(new CircleTransform())
                .into(leftIcon);
    }


    public static ToolbarFragment newInstance() {
        ToolbarFragment fragment = new ToolbarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get parameters from arguments
        }
    }

    public void setListener(ToolbarFragmentListener toolbarFragmentListener){
        this.toolbarFragmentListener = toolbarFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toolbar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.hideOverflowMenu();
        leftIcon = (ImageView)view.findViewById(R.id.toolbar_left_icon);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarFragmentListener.onLeftIconPressed();
            }
        });
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolbarFragmentListener = null;
    }

    public interface ToolbarFragmentListener {
        void onLeftIconPressed();
    }
}
