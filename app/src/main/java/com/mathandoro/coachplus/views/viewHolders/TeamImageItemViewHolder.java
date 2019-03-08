package com.mathandoro.coachplus.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mathandoro.coachplus.R;

public class TeamImageItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView teamImage;
    public TeamImageItemViewHolder(View view) {
        super(view);
        teamImage = view.findViewById(R.id.team_feed_team_image);
    }
}
