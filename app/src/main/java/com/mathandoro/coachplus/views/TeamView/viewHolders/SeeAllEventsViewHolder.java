package com.mathandoro.coachplus.views.TeamView.viewHolders;

import android.view.View;
import android.widget.Button;

import com.mathandoro.coachplus.R;

import androidx.recyclerview.widget.RecyclerView;

public class SeeAllEventsViewHolder  extends RecyclerView.ViewHolder {

    public interface  SeeAllEventsListener{
        void onSeeAllEvents();
    }

    public View itemContainer;
    public Button seeAllEventsButton;


    public SeeAllEventsViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        this.seeAllEventsButton = view.findViewById(R.id.team_view_see_all_events_button);
    }

    public void bind(SeeAllEventsListener listener){
        seeAllEventsButton.setOnClickListener(view -> listener.onSeeAllEvents());
    }
}

