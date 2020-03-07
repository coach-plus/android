package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 24.03.17.
 */

public class RegisterUser {
    protected String firstname;
    protected String lastname;
    protected String email;
    protected String password;
    protected boolean termsAccepted;
    protected boolean dataPrivacyAccepted;


    public RegisterUser(String firstname, String lastname, String email, String password, boolean termsAccepted, boolean dataPrivacyAccepted) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.termsAccepted = termsAccepted;
        this.dataPrivacyAccepted = dataPrivacyAccepted;
    }
}
