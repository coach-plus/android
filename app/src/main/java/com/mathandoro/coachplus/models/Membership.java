package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mathandoro.coachplus.Role;

/**
 * Created by dominik on 31.03.17.
 */

public class Membership implements Parcelable {
    protected String _id;
    protected String role;
    protected Team team;
    protected String user;
    protected boolean joined;

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public Membership(String role, Team team, String user, int memberCount, boolean joined) {
        this.role = role;
        this.team = team;
        this.user = user;
        this.joined = joined;
    }

    protected Membership(Parcel in) {
        _id = in.readString();
        role = in.readString();
        team = in.readParcelable(Team.class.getClassLoader());
        user = in.readString();
        joined = in.readByte() != 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(role);
        dest.writeParcelable(team, flags);
        dest.writeString(user);
        dest.writeByte((byte) (joined ? 1 : 0));
    }

    @Override
    public int describeContents() {
            return 0;
    }

    public static final Creator<Membership> CREATOR = new Creator<Membership>() {
        @Override
        public Membership createFromParcel(Parcel in) {
                return new Membership(in);
        }

        @Override
        public Membership[] newArray(int size) {
                return new Membership[size];
        }
    };

    public String getRole() {
            return role;
    }

    public void setRole(String role) {
            this.role = role;
    }

    public Team getTeam() {
            return team;
    }

    public void setTeam(Team team) {
            this.team = team;
    }

    public String getUser() {
            return user;
    }

    public void setUser(String user) {
            this.user = user;
    }

    public static Creator<Membership> getCREATOR() {
            return CREATOR;
    }

    public boolean isCoach(){
        return this.role.equals(Role.COACH);
    }

    public String getId() {
        return _id;
    }
}
