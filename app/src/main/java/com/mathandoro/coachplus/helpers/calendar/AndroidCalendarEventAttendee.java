package com.mathandoro.coachplus.helpers.calendar;

import android.content.ContentValues;
import android.provider.CalendarContract;

/**
 * Created by dominik on 24.04.18.
 */

public class AndroidCalendarEventAttendee {
    private String name;
    private String email;
    private String eventId;
    private int attendeeStatus;

    public static final int ATTENDEE_STATUS_ACCEPTED = 1;
    public static final int ATTENDEE_STATUS_DECLINED = 2;
    public static final int ATTENDEE_STATUS_INVITED = 3;
    public static final int ATTENDEE_STATUS_NONE = 0;
    public static final int ATTENDEE_STATUS_TENTATIVE = 4;

    public AndroidCalendarEventAttendee(String name, String email, String eventId, int attendeeStatus) {
        this.name = name;
        this.email = email;
        this.eventId = eventId;
        this.attendeeStatus = attendeeStatus;
    }

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_REQUIRED);
        values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, name + "@coach.plus"); // todo check!
        values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
        values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
        values.put(CalendarContract.Attendees.EVENT_ID, eventId);

        return values;
    }
}
