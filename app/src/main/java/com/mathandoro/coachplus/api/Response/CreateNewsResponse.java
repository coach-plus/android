package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.News;

public class CreateNewsResponse {
    private News news;

    public CreateNewsResponse(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}
