package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.comments.VideoComment;
import com.tecnocampus.LS2.protube_back.comments.VideoCommentRepository;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
public class CommentsController {
    private final VideoCommentRepository comments;
    private final VideoService videos;
    public CommentsController(VideoCommentRepository comments, VideoService videos) {
        this.comments = comments; this.videos = videos;
    }
    public record CommentInput(@NotBlank @Size(max = 2000) String content) {}
    @GetMapping
    public List<VideoComment> list(@PathVariable String videoId) {
        return comments.findByVideoIdOrderByCreatedAtAsc(videoId);
    }
    @PostMapping
    public ResponseEntity<?> create(@PathVariable String videoId, @Valid @RequestBody CommentInput input,
                                    Authentication authentication) {
        if (videos.getVideos().stream().noneMatch(v -> videoId.equals(v.get("id"))))
            return ResponseEntity.notFound().build();
        return ResponseEntity.status(201).body(comments.save(new VideoComment(videoId,
                authentication.getName(), input.content().trim())));
    }
}
