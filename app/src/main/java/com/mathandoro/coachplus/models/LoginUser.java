package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 26.03.17.
 */

public class LoginUser {
    protected String email;
    protected String password;

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
