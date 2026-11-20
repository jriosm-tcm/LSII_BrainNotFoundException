package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.CommentResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentsManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.CreateCommentCommand;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
public class CommentsController {

    private final CommentsManagementUseCase commentsUseCase;

    public CommentsController(CommentsManagementUseCase commentsUseCase) {
        this.commentsUseCase = commentsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CommentResult>> getComments(
            @PathVariable("videoId") Long videoId
    ) {
        return ResponseEntity.of(
                commentsUseCase.getComments(videoId)
        );
    }

    @PostMapping
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<CommentResult> createComment(
            @PathVariable("videoId") Long videoId,
            @Valid @RequestBody CreateCommentCommand command,
            Authentication authentication
    ) {
        return commentsUseCase.createComment(
                        videoId,
                        authentication.getName(),
                        command
                )
                .map(comment -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(comment))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
