package com.mathandoro.coachplus.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.mathandoro.coachplus.R;

public class UpcomingEventsHeaderViewHolder extends RecyclerView.ViewHolder {
    public Button seeAllEventsButton;

    public UpcomingEventsHeaderViewHolder(View view) {
        super(view);
        seeAllEventsButton = view.findViewById(R.id.team_feed_upcoming_events_header_see_all_events_button);
    }
}
