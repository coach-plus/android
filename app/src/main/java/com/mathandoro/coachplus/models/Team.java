package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dominik on 31.03.17.
 */

public class Team implements Parcelable{
    protected String _id;
    protected String name;
    protected boolean isPublic;

    public Team(String _id, String name, boolean isPublic) {
        this._id = _id;
        this.name = name;
        this.isPublic = isPublic;
    }

    protected Team(Parcel in) {
        _id = in.readString();
        name = in.readString();
        isPublic = in.readByte() != 0;
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeByte((byte) (isPublic ? 1 : 0));
    }
}
