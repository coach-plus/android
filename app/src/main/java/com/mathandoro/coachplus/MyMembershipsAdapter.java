package com.mathandoro.coachplus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathandoro.coachplus.helpers.CircleTransform;
import com.mathandoro.coachplus.models.Membership;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominik on 31.03.17.
 */


public class MyMembershipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainActivity mainActivity;
    private List<Membership> memberships;

    final int TEAM_ITEM = 0;

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView teamNameTextView;
        public ImageView teamImageView;
        public View containerView;

        public TeamViewHolder(View view) {
            super(view);
            containerView = view;
            teamNameTextView = (TextView)view.findViewById(R.id.team_item_team_name);
            teamImageView = (ImageView)view.findViewById(R.id.team_item_team_icon);
        }

        public void bindMembership(Membership membership){
            if(membership.getTeam().getImage() != null){
                String imageUrl = BuildConfig.BASE_URL + "/uploads/" + membership.getTeam().getImage();
                Picasso.with(teamImageView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.circle)
                        .transform(new CircleTransform())
                        .into(teamImageView);
            }
            else{
                // cancel any pending request if there is one.
                // This can happen because when data gets updated within a short period (first cache, then live data!)
                Picasso.with(teamImageView.getContext()).cancelRequest(teamImageView);
                teamImageView.setImageResource(R.drawable.circle);
            }
        }
    }


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
                        .inflate(R.layout.navigation_item, parent, false);
                viewHolder = new TeamViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TEAM_ITEM:
                final Membership membership = memberships.get(position);
                final TeamViewHolder teamViewHolder = (TeamViewHolder)holder;
                teamViewHolder.teamNameTextView.setText(membership.getTeam().getName());
                teamViewHolder.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.switchTeamContext(membership);
                    }
                });
               teamViewHolder.bindMembership(membership);
        }
    }



    @Override
    public int getItemCount() {
        return memberships.size() ;
    }
}