package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.models.Membership;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 31.03.17.
 */


public class MyMembershipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainActivity mainActivity;
    private List<Membership> memberships;

    final int TEAM_ITEM = 0;
    final int CREATE_TEAM_ITEM = 1;

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView teamNameTextView;
        public View containerView;

        public TeamViewHolder(View view) {
            super(view);
            containerView = view;
            teamNameTextView = (TextView)view.findViewById(R.id.team_item_team_name);
        }
    }

    public static class RegisterTeamViewHolder extends RecyclerView.ViewHolder {
        public View containerView;

        public RegisterTeamViewHolder(View view) {
            super(view);
            containerView = view;
        }
    }

    // todo additional view holders for create team, logout etc.

    public MyMembershipsAdapter(MainActivity mainActivity) {
        this.memberships = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    public void setMemberships(List<Membership> memberships){
        this.memberships = memberships;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position < this.memberships.size()){
            return TEAM_ITEM;
        }
        return CREATE_TEAM_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch(viewType){
            case CREATE_TEAM_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.register_team_item, parent, false);
                viewHolder = new RegisterTeamViewHolder(view);
                break;
            case TEAM_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.navigation_item, parent, false);
                viewHolder = new TeamViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case CREATE_TEAM_ITEM:
                RegisterTeamViewHolder registerTeamViewHolder = (RegisterTeamViewHolder) holder;
                registerTeamViewHolder.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.navigateToCreateTeamActivity();
                    }
                });
                break;

            case TEAM_ITEM:
                TeamViewHolder teamViewHolder = (TeamViewHolder)holder;
                teamViewHolder.teamNameTextView.setText(memberships.get(position).getTeam().getName());
                teamViewHolder.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.switchTeamContext(memberships.get(position));
                    }
                });
        }
    }



    @Override
    public int getItemCount() {
        return memberships.size() + 1;
    }
}