package com.mathandoro.coachplus.views.EventDetail.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.Formatter;
import com.mathandoro.coachplus.models.Event;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EventDetailHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView time;
    TextView location;
    TextView description;

    public EventDetailHeaderViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.event_detail_attendance_sub_header_text);
        location = view.findViewById(R.id.event_detail_location);
        time = view.findViewById(R.id.event_detail_time);
        description = view.findViewById(R.id.event_detail_description);
    }

    public void bind(Event event){
        name.setText(event.getName());
        if(event.getLocation() == null || event.getLocation().getName().equals("")) {
            location.setText(R.string.No_Location);
        }
        else {
            location.setText(event.getLocation().getName());
            String locationUrlEncoded = null;
            try {
                locationUrlEncoded = URLEncoder.encode(event.getLocation().getName(), "utf-8");
                location.setText(Html.fromHtml("<a href=https://www.google.com/maps/search/?api=1&query=" + locationUrlEncoded + ">" + event.getLocation().getName() + "</a>"));
                location.setMovementMethod(LinkMovementMethod.getInstance());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(event.getStart() != null){
            time.setText(Formatter.formatGermanTimestamp(event.getStart(), event.getEnd(), itemView.getContext()));
        }
        description.setText(event.getDescription());
    }
}

