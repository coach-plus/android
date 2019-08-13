package com.mathandoro.coachplus.views.EventDetail.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.Formatter;
import com.mathandoro.coachplus.models.Event;

public class EventDetailHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView time;
    TextView location;
    TextView description;

    public EventDetailHeaderViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.event_detail_attendance_header_text);
        location = view.findViewById(R.id.event_detail_location);
        time = view.findViewById(R.id.event_detail_time);
        description = view.findViewById(R.id.event_detail_description);
    }

    public void bind(Event event){
        name.setText(event.getName());
        if(event.getLocation() != null){
            location.setText(event.getLocation().getName());
            // todo location google maps link!
        }
        if(event.getStart() != null){
            time.setText(Formatter.formatGermanTimestamp(event.getStart(), event.getEnd(), itemView.getContext()));
        }
        description.setText(event.getDescription());
    }
}

