package com.mathandoro.coachplus.views.TeamView;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Membership;
import com.mathandoro.coachplus.views.viewHolders.MembershipViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 31.03.17.
 */


public class MyMembershipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final TeamViewActivity mainActivity;
    private List<Membership> memberships;

    final int TEAM_ITEM = 0;

    public MyMembershipsAdapter(TeamViewActivity mainActivity) {
        this.memberships = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    public void setMemberships(List<Membership> memberships){
        this.memberships = memberships;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TEAM_ITEM;
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
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_ITEM:
                final Membership membership = memberships.get(position);
                MembershipViewHolder teamViewHolder = (MembershipViewHolder)holder;
                teamViewHolder.containerView.setOnClickListener((View v) -> mainActivity.switchTeamContext(membership));
                teamViewHolder.bind(membership, false,true, null); // todo dont show icons here
        }
    }

    @Override
    public int getItemCount() {
        return memberships.size() ;
    }
}