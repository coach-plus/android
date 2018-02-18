package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.Event;

import java.util.List;

/**
 * Created by d059458 on 13.04.17.
 */

public class EventsResponse {
    List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
