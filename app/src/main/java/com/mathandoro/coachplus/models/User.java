package com.mathandoro.coachplus.models;

import java.util.Date;

/**
 * Created by dominik on 31.03.17.
 */

public class User {
    protected String firstname;
    protected String lastname;
    protected String email;
    protected Date registered;
    protected boolean emailVerified;
    protected String image;
    protected String _id;

    public User(String id, String firstname, String lastname, String email, String image, Date registered, boolean emailVerified) {
        this._id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.registered = registered;
        this.emailVerified = emailVerified;
        this.image = image;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
