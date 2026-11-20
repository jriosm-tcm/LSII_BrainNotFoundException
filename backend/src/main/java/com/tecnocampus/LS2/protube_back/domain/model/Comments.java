package com.tecnocampus.LS2.protube_back.domain.model;

import java.time.LocalDateTime;

public class Comments {

    private final Long id;
    private final Long videoId;
    private final String authorId;
    private final String authorName;
    private final String content;
    private final LocalDateTime createdAt;

    public Comments(
            Long id,
            Long videoId,
            String authorId,
            String authorName,
            String content,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.videoId = videoId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {return id;}

    public Long getVideoId() {return videoId;}

    public String getAuthorId() {return authorId;}

    public String getAuthorName() {return authorName;}

    public String getContent() {return content;}

    public LocalDateTime getCreatedAt() {return createdAt;}
}
