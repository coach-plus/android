package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dominik on 07.05.18.
 */

public class Location implements Parcelable {
    protected String name;
    protected float latitude;
    protected float longitude;

    public Location(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }

    protected Location(Parcel in) {
        name = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }


    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}

