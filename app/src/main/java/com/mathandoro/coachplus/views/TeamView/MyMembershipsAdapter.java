package com.mathandoro.coachplus.views.TeamView;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.RecycleViewStack;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.viewHolders.MembershipViewHolder;
import com.mathandoro.coachplus.views.viewHolders.StaticViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 31.03.17.
 */


public class MyMembershipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final TeamViewActivity mainActivity;
    private List<Membership> memberships;
    private RecycleViewStack viewStack;

    final int TEAM_ITEM = 0;
    final int NO_TEAMS_ITEM = 1;

    public MyMembershipsAdapter(TeamViewActivity mainActivity) {
        this.memberships = new ArrayList<>();
        this.mainActivity = mainActivity;
        this.viewStack = new RecycleViewStack();
        this.viewStack.addSection(TEAM_ITEM, 0);
        this.viewStack.addSection(NO_TEAMS_ITEM, 0);
    }

    public void setMemberships(List<Membership> memberships){
        this.memberships = memberships;
        this.viewStack.updateSection(TEAM_ITEM, memberships.size());
        if(this.memberships.size() == 0){
            this.viewStack.updateSection(NO_TEAMS_ITEM, 1);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewStack.getSectionIdAt(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch(viewType){
            case TEAM_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.team_item, parent, false);
                viewHolder = new MembershipViewHolder(view);
                break;
            case NO_TEAMS_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_no_teams, parent, false);
                view.findViewById(R.id.no_teams_create_team_button).setVisibility(View.INVISIBLE);
                ((TextView)(view.findViewById(R.id.no_teams_text))).setText(R.string.no_team_text_memberships);
                viewHolder = new StaticViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_ITEM:
                final Membership membership = memberships.get(position);
                MembershipViewHolder teamViewHolder = (MembershipViewHolder)holder;
                teamViewHolder.containerView.setOnClickListener((View v) -> {
                    mainActivity.switchTeamContext(membership);
                });
                teamViewHolder.bind(membership, false,true, true, null); // todo dont show icons here
                break;
            case NO_TEAMS_ITEM:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return viewStack.size() ;
    }
}