package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 28.04.17.
 */

public class RegisterTeam {
    protected String name;
    protected boolean isPublic;
    protected String image;

    public RegisterTeam(String name, boolean isPublic, String image) {
        this.name = name;
        this.isPublic = isPublic;
        this.image = image;
    }
}
