package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dominik on 02.04.17.
 */

public class Event implements Parcelable {
    protected String name;
    protected String description;
    protected Date start;
    protected Date end;
    protected String location;

    public Event(String name, String description, Date start, Date end, String location) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.location = location;
    }

    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        start = (Date)in.readValue(Date.class.getClassLoader());
        end = (Date)in.readValue(Date.class.getClassLoader());
        location = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeValue(start);
        dest.writeValue(end);
        dest.writeString(location);
    }
}
