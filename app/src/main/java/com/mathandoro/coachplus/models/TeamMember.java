package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d059458 on 03.04.17.
 */

public class TeamMember implements Parcelable {
    protected String _id;
    protected String role;
    protected ReducedUser user;


    public TeamMember(String _id, String role, ReducedUser user) {
        this._id = _id;
        this.role = role;
        this.user = user;
    }

    protected TeamMember(Parcel in) {
        _id = in.readString();
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

    public String get_id() {
        return _id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(_id);
        parcel.writeString(role);
        parcel.writeParcelable(user, flags);
    }
}

