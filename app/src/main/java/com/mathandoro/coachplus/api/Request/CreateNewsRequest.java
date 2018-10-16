package com.mathandoro.coachplus.api.Request;

import com.mathandoro.coachplus.models.ReducedUser;

public class CreateNewsRequest {
    String title;
    String text;

    public CreateNewsRequest(String title, String text) {
        this.title = title;
        this.text = text;
    }
}
