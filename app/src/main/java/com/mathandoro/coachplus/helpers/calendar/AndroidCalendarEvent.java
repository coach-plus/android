package com.mathandoro.coachplus.helpers.calendar;

import android.content.ContentValues;
import android.provider.CalendarContract;

import java.util.Calendar;

/**
 * Created by dominik on 24.04.18.
 */

public class AndroidCalendarEvent {
    private long calID;
    private Calendar beginTime;
    private Calendar endTime;
    private String timezone; //= "Europe/Berlin"
    private String title;
    private String description;

    public AndroidCalendarEvent(long calID, Calendar beginTime, Calendar endTime, String timezone, String title, String description) {
        this.calID = calID;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.timezone = timezone;
        this.title = title;
        this.description = description;
    }

    public ContentValues toContentValues(){
        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

        return values;
    }
}
