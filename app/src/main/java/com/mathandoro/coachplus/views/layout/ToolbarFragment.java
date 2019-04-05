package com.mathandoro.coachplus.views.layout;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Team;


public class ToolbarFragment extends Fragment {
    private Toolbar toolbar;
    private TextView leftIcon;
    private TextView rightIcon;
    private TextView title;

    private ToolbarFragmentListener toolbarFragmentListener;

    public ToolbarFragment() {
        // Required empty public constructor
    }

    public void showMenuButton(){
        this.leftIcon.setText(R.string.fa_tshirt);
    }

    public void showBackButton(){
        this.leftIcon.setText(R.string.fa_arrow_left);
    }

    public void showUserIcon(){
        this.rightIcon.setVisibility(View.VISIBLE);
        this.rightIcon.setText(R.string.fa_user);
    }

    public void showSettings(){
        this.rightIcon.setVisibility(View.VISIBLE);
        this.rightIcon.setText(R.string.fa_cog);
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
