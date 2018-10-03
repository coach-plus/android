package com.mathandoro.coachplus.models;

public class Participation {
    protected String user;
    protected String event;
    protected Boolean willAttend;
    protected Boolean didAttend;

    public Participation(String user, String event, Boolean willAttend, Boolean didAttend) {
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

    public Boolean WillAttend() {
        return willAttend;
    }

    public Boolean DidAttend() {
        return didAttend;
    }
}
