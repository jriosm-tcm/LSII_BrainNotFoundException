package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikeResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikesResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikesUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos/{videoId}/comments/{commentId}/likes")
public class CommentLikesController {

    private final CommentLikesUseCase likesUseCase;

    public CommentLikesController(CommentLikesUseCase likesUseCase) {
        this.likesUseCase = likesUseCase;
    }

    @GetMapping
    public ResponseEntity<CommentLikesResult> getLikes(@PathVariable("videoId") Long videoId, @PathVariable("commentId") Long commentId) {
        return ResponseEntity.of(likesUseCase.getLikes(videoId, commentId));
    }

    @PostMapping
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<CommentLikeResult> toggleLike(@PathVariable("videoId") Long videoId, @PathVariable("commentId") Long commentId, Authentication authentication) {
        return ResponseEntity.of(
                likesUseCase.toggleLike(videoId, commentId, authentication.getName())
        );
    }
}
