package com.mathandoro.coachplus.views.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Team;


public class ToolbarFragment extends Fragment {
    private Toolbar toolbar;
    private ImageView leftIcon;
    private ImageView rightIcon;
    private TextView title;

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

    public void showUserIcon(){
        this.rightIcon.setVisibility(View.VISIBLE);
        this.rightIcon.setImageResource(R.drawable.ic_account_circle_white_36dp);
    }

    public void setTeam(Team team){
       title.setText(team.getName());
    }

    public void setTitle(String titleText){
        title.setText(titleText);
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
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.hideOverflowMenu();
        title = view.findViewById(R.id.toolbar_team_name);
        leftIcon = view.findViewById(R.id.toolbar_left_icon);
        rightIcon = view.findViewById(R.id.toolbar_right_icon);
        rightIcon.setVisibility(View.GONE);
        leftIcon.setOnClickListener((View v) -> {
            if(toolbarFragmentListener == null){
                return;
            }
            toolbarFragmentListener.onLeftIconPressed();
        });
        rightIcon.setOnClickListener((View v) ->{
            if(toolbarFragmentListener == null){
                return;
            }
            toolbarFragmentListener.onRightIconPressed();
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
        void onRightIconPressed();
    }
}
