package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.comments.VideoComment;
import com.tecnocampus.LS2.protube_back.comments.VideoCommentRepository;
import com.tecnocampus.LS2.protube_back.comments.CommentLike;
import com.tecnocampus.LS2.protube_back.comments.CommentLikeRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final CommentLikeRepository likes;
    public CommentsController(VideoCommentRepository comments, VideoService videos, CommentLikeRepository likes) {
        this.comments = comments; this.videos = videos; this.likes = likes;
    }
    public record CommentInput(@NotBlank @Size(max = 2000) String content) {}
    public record ReplyInput(@NotBlank @Size(max=2000) String content) {}
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<?> reply(@PathVariable String videoId, @PathVariable Long commentId,
                                   @Valid @RequestBody ReplyInput input, Authentication authentication) {
        var parent = comments.findById(commentId);
        if (parent.isEmpty() || !parent.get().getVideoId().equals(videoId))
            return ResponseEntity.notFound().build();
        return ResponseEntity.status(201).body(comments.save(new VideoComment(videoId,
                authentication.getName(), input.content().trim(), commentId)));
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<?> likeCount(@PathVariable String videoId,@PathVariable Long commentId) {
        var comment=comments.findById(commentId);
        if(comment.isEmpty() || !comment.get().getVideoId().equals(videoId))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("likes",likes.countByCommentId(commentId)));
    }

    @PostMapping("/{commentId}/likes")
    @Transactional
    public ResponseEntity<?> toggleLike(@PathVariable String videoId,@PathVariable Long commentId,Authentication authentication) {
        var comment=comments.findById(commentId);
        if(comment.isEmpty() || !comment.get().getVideoId().equals(videoId))
            return ResponseEntity.notFound().build();
        String user=authentication.getName();
        if(likes.existsByCommentIdAndUsername(commentId,user))
            likes.deleteByCommentIdAndUsername(commentId,user);
        else likes.save(new CommentLike(commentId,user));
        return ResponseEntity.ok(Map.of("likes",likes.countByCommentId(commentId),
                "liked",likes.existsByCommentIdAndUsername(commentId,user)));
    }

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
