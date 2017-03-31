package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 31.03.17.
 */

public class Membership {
    protected String role;
    protected Team team;
    protected User user;

    public Membership(String role, Team team, User user) {
        this.role = role;
        this.team = team;
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
