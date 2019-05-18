package com.mathandoro.coachplus.views.viewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.helpers.Formatter;
import com.mathandoro.coachplus.models.Event;

public class EventItemViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView location;
    public TextView time;
    public View itemContainer;
    public TextView calendarMonth;
    public TextView calendarDay;


    public EventItemViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        this.title = view.findViewById(R.id.event_item_event_name);
        this.time = view.findViewById(R.id.event_item_event_start);
        this.location = view.findViewById(R.id.event_item_event_location);
        this.calendarMonth = view.findViewById(R.id.event_item_calendar_month_text_view);
        this.calendarDay = view.findViewById(R.id.event_item_calendar_day_text_view);
    }

    public void bind(Event event){
        this.title.setText(event.getName());
        if(event.getLocation() != null){
            this.location.setText(event.getLocation().getName());
        }
        else{
            location.setText("");
        }
        if(event.getStart() != null){
            this.time.setText(Formatter.formatGermanTimestamp(event.getStart(), event.getEnd()));
            this.calendarDay.setText(Formatter.formatMonthDay(event.getStart()));
            this.calendarMonth.setText(Formatter.formatfullMonthName(event.getStart()));
        }
    }


}