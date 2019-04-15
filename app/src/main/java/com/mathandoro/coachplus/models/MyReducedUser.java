package com.mathandoro.coachplus.models;

import android.os.Parcel;

public class MyReducedUser extends ReducedUser {
    private String email;
    private boolean emailVerified;


    public MyReducedUser(String firstname, String lastname, String _id, String image,
                         String email, boolean emailVerified) {
        super(firstname, lastname, _id, image);
        this.email = email;
        this.emailVerified = emailVerified;
    }

    protected MyReducedUser(Parcel in) {
        super(in);
        this.email = in.readString();
        this.emailVerified = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(email);
        parcel.writeByte((byte) (emailVerified ? 1 : 0));
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}