package com.mathandoro.coachplus.api.Response;

import com.mathandoro.coachplus.models.News;

import java.util.List;

public class GetNewsResponse {
    private List<News> news;

    public GetNewsResponse(List<News> news) {
        this.news = news;
    }

    public List<News> getNews() {
        return news;
    }
}
