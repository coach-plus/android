package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 31.03.17.
 */

public class Team {
    protected String name;
    protected boolean isPublic;

    public Team(String name, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
