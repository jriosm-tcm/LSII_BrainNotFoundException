package com.tecnocampus.LS2.protube_back.comments;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "video_comments")
public class VideoComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String videoId;
    @Column(nullable = false) private String author;
    @Column(nullable = false, length = 2000) private String content;
    @Column(nullable = false) private Instant createdAt = Instant.now();
    protected VideoComment() {}
    public VideoComment(String videoId, String author, String content) {
        this.videoId = videoId; this.author = author; this.content = content;
    }
    public Long getId() { return id; }
    public String getVideoId() { return videoId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
