package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d059458 on 03.04.17.
 */

public class ReducedUser implements Parcelable {
    protected String firstname;
    protected String lastname;
    protected String _id;

    public ReducedUser(String firstname, String lastname, String _id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this._id = _id;
    }

    protected ReducedUser(Parcel in) {
        firstname = in.readString();
        lastname = in.readString();
        _id = in.readString();
    }

    public static final Creator<ReducedUser> CREATOR = new Creator<ReducedUser>() {
        @Override
        public ReducedUser createFromParcel(Parcel in) {
            return new ReducedUser(in);
        }

        @Override
        public ReducedUser[] newArray(int size) {
            return new ReducedUser[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(_id);
    }
}
