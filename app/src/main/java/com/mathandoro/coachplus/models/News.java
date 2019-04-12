package com.mathandoro.coachplus.models;

import java.util.Date;

public class News {
    private String _id;
    private String event;
    private ReducedUser author;
    private Date created;
    private String text;
    private String title;

    public News(String event, ReducedUser author, Date created, String text, String title) {
        this.event = event;
        this.author = author;
        this.created = created;
        this.text = text;
        this.title = title;
    }

    public String getEvent() {
        return event;
    }

    public ReducedUser getAuthor() {
        return author;
    }

    public Date getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return _id;
    }
}
