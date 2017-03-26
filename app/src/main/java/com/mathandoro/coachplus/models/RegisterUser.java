package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 24.03.17.
 */

public class RegisterUser {
    protected String firstname;
    protected String lastname;
    protected String email;
    protected String password;

    public RegisterUser(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
