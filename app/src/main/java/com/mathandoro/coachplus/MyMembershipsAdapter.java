package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathandoro.coachplus.models.Membership;

/**
 * Created by dominik on 31.03.17.
 */


public class MyMembershipsAdapter extends RecyclerView.Adapter<MyMembershipsAdapter.ViewHolder> {
    private Membership[] memberships;

    final int TEAM_ITEM = 0;
    final int CREATE_TEAM_ITEM = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView teamNameTextView;

        public ViewHolder(View view) {
            super(view);
            teamNameTextView = (TextView)view.findViewById(R.id.team_item_team_name);
        }
    }

    // todo additional view holders for create team, logout etc.

    public MyMembershipsAdapter() {
        memberships = new Membership[0];
    }

    public void setMemberships(Membership[] memberships){
        this.memberships = memberships;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TEAM_ITEM;
    }

    @Override
    public MyMembershipsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.teamNameTextView.setText(memberships[position].getTeam().getName());
    }

    @Override
    public int getItemCount() {
        return memberships.length;
    }
}