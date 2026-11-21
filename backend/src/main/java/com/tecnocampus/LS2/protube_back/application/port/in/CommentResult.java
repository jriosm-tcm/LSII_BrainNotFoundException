package com.tecnocampus.LS2.protube_back.application.port.in;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CommentResult(
        Long id,
        Long videoId,
        Long parentId,
        String authorId,
        String authorName,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime createdAt
) {
}