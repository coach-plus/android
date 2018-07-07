package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dominik on 31.03.17.
 */

public class Membership implements Parcelable {
        protected String role;
        protected Team team;
        protected String user;
        protected int memberCount;

        public Membership(String role, Team team, String user, int memberCount) {
                this.role = role;
                this.team = team;
                this.user = user;
                this.memberCount = memberCount;
        }

        protected Membership(Parcel in) {
                role = in.readString();
                team = in.readParcelable(Team.class.getClassLoader());
                user = in.readString();
                memberCount = in.readInt();
        }


        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(role);
                dest.writeParcelable(team, flags);
                dest.writeString(user);
                dest.writeInt(memberCount);
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

        public int getMemberCount() {
                return memberCount;
        }

        public void setMemberCount(int memberCount) {
                this.memberCount = memberCount;
        }

        public static Creator<Membership> getCREATOR() {
                return CREATOR;
        }
}
