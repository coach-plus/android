package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 28.04.17.
 */

public class RegisterTeam {
    protected String name;
    protected boolean isPublic;

    public RegisterTeam(String name, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }
}
