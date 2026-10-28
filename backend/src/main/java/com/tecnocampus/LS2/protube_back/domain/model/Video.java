package com.tecnocampus.LS2.protube_back.domain.model;

public class Video {

    private Long id;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;

    public Video(Long id, String title, String description, String url, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
