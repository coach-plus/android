package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.Event;

/**
 * Created by d059458 on 13.04.17.
 */

public class CreateEventResponse {
    protected Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
