package com.mathandoro.coachplus.models;

import java.util.List;

public class MyMembershipsResponse {

    protected List<Membership> memberships;

    public MyMembershipsResponse(List<Membership> memberships) {
        this.memberships = memberships;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }
}
