package com.mathandoro.coachplus.models;

public class Participation {
    protected String user;
    protected String event;
    protected boolean willAttend;
    protected boolean didAttend;

    public Participation(String user, String event, boolean willAttend, boolean didAttend) {
        this.user = user;
        this.event = event;
        this.willAttend = willAttend;
        this.didAttend = didAttend;
    }

    public String getUser() {
        return user;
    }

    public String getEvent() {
        return event;
    }

    public boolean WillAttend() {
        return willAttend;
    }

    public boolean DidAttend() {
        return didAttend;
    }
}
