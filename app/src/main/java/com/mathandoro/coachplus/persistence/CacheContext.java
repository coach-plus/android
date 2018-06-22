package com.mathandoro.coachplus.persistence;

import com.mathandoro.coachplus.models.Team;

/**
 * Created by dominik on 01.04.17.
 */

public class CacheContext {

    String directory;

    public CacheContext(String directory) {
        this.directory = directory;
    }

    public String getDirectory(){
        return directory;
    }

    public static CacheContext DEFAULT(){
        return new CacheContext("");
    }

    public static CacheContext TEAM(Team team){
        return new CacheContext("teams/" + team.getName());
    }
}
