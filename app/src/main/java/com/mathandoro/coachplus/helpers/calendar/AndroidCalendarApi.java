package com.mathandoro.coachplus.helpers.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;

/**
 * Created by dominik on 24.04.18.
 */

public class AndroidCalendarApi {


    private ContentResolver contentResolver;

    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    public AndroidCalendarApi(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public ArrayList<AndroidCalendar> getCalendars(){
        Cursor cursor = null;
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        cursor = this.contentResolver.query(uri, CALENDAR_PROJECTION, null, null, null);
        ArrayList<AndroidCalendar> calendars = new ArrayList<>();

        while (cursor.moveToNext()) {
            AndroidCalendar calendar = AndroidCalendar.fromCursor(cursor);
            calendars.add(calendar);
        }
        return calendars;
    }

    public String createCalendarEvent(AndroidCalendarEvent event){
        Uri uri = this.contentResolver.insert(CalendarContract.Events.CONTENT_URI, event.toContentValues());
        String eventID = uri.getLastPathSegment();
        return eventID;
    }

    public void addAttendee(AndroidCalendarEventAttendee attendee) {
        Uri uri = this.contentResolver.insert(CalendarContract.Attendees.CONTENT_URI, attendee.toContentValues());
    }
}
