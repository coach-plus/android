package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 14.05.18.
 */

public class JWTUser {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String image;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getImage() {
        return image;
    }
}
