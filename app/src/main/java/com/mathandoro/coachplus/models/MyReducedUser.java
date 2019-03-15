package com.mathandoro.coachplus.models;

import android.os.Parcel;

public class MyReducedUser extends ReducedUser {
    private String email;

    public MyReducedUser(String firstname, String lastname, String _id, String image, String email) {
        super(firstname, lastname, _id, image);
        this.email = email;
    }

    protected MyReducedUser(Parcel in) {
        super(in);
        this.email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(email);
    }

    public String getEmail() {
        return email;
    }
}