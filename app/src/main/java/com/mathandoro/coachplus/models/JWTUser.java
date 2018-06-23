package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 14.05.18.
 */

public class JWTUser extends ReducedUser {
    protected String email;

    public JWTUser(String firstname, String lastname, String _id, String image, String email) {
        super(firstname, lastname, _id, image);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
