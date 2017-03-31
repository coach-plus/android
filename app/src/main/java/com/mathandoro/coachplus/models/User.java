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

    public User(String firstname, String lastname, String email, Date registered, boolean emailVerified) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.registered = registered;
        this.emailVerified = emailVerified;
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
}
