package com.mathandoro.coachplus.models;

public class MyMembershipsResponse {
    protected MyMembership[] memberships;

    public MyMembershipsResponse(MyMembership[] memberships) {
        this.memberships = memberships;
    }

    public MyMembership[] getMemberships() {
        return memberships;
    }

    public void setMemberships(MyMembership[] memberships) {
        this.memberships = memberships;
    }
}
