package com.mathandoro.coachplus.helpers.calendar;

import android.database.Cursor;

/**
 * Created by dominik on 24.04.18.
 */

public class AndroidCalendar {
    private String displayName;
    private String accountName;
    private String ownerName;
    private String id;

    final static int PROJECTION_ID_INDEX = 0;
    final static int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    final static int PROJECTION_DISPLAY_NAME_INDEX = 2;
    final static int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public AndroidCalendar(String id, String displayName, String accountName, String ownerName) {
        this.id = id;
        this.displayName = displayName;
        this.accountName = accountName;
        this.ownerName = ownerName;
    }

    public static AndroidCalendar fromCursor(Cursor cursor){
        String calendarId = ""+ cursor.getLong(PROJECTION_ID_INDEX); // getString ?
        String displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
        String accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
        String ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
        return new AndroidCalendar(calendarId, displayName, accountName, ownerName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
