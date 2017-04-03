package com.mathandoro.coachplus.models;

import java.util.List;

/**
 * Created by d059458 on 03.04.17.
 */


public class TeamMembersResponse {
    protected List<TeamMember> members;

    public TeamMembersResponse(List<TeamMember> members) {
        this.members = members;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }
}
