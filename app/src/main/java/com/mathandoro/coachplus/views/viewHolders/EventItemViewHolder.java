package com.mathandoro.coachplus.views.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.models.Event;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventItemViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView location;
    public TextView time;
    public View itemContainer;

    public EventItemViewHolder(View view) {
        super(view);
        this.itemContainer = view;
        this.title = view.findViewById(R.id.event_item_event_name);
        this.time = view.findViewById(R.id.event_item_event_start);
        this.location = view.findViewById(R.id.event_item_event_location);
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
            this.time.setText(this.formatGermanTimestamp(event.getStart(), event.getEnd()));
        }
    }

    private String formatGermanTimestamp(Date startDate, Date endDate)
    {
        String dateString;
        String longFormat = "dd.MM.yy - HH:mm";
        String shortFormat = "HH:mm";
        SimpleDateFormat formatedStartDate = new SimpleDateFormat(longFormat);
        SimpleDateFormat formatedEndDate =  new SimpleDateFormat(longFormat);
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTime(endDate);
        endCalendar.setTime(endDate);

        if(startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)){
            formatedEndDate = new SimpleDateFormat(shortFormat);
        }

        try{
            dateString = formatedStartDate.format( startDate ) + " Uhr bis " + formatedEndDate.format(endDate) + " Uhr";
        }catch (Exception exception){
            dateString = "?";
        }
        return dateString;
    }
}