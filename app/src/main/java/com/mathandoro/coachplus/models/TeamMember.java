package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d059458 on 03.04.17.
 */

public class TeamMember implements Parcelable {
    protected String role;
    protected ReducedUser user;


    public TeamMember(String role, ReducedUser user) {
        this.role = role;
        this.user = user;
    }

    protected TeamMember(Parcel in) {
        role = in.readString();
        user = in.readParcelable(ReducedUser.class.getClassLoader());
    }

    public static final Creator<TeamMember> CREATOR = new Creator<TeamMember>() {
        @Override
        public TeamMember createFromParcel(Parcel in) {
            return new TeamMember(in);
        }

        @Override
        public TeamMember[] newArray(int size) {
            return new TeamMember[size];
        }
    };

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ReducedUser getUser() {
        return user;
    }

    public void setUser(ReducedUser user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(role);
        parcel.writeParcelable(user, flags);
    }
}

