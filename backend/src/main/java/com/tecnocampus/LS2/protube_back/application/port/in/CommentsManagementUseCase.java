package com.tecnocampus.LS2.protube_back.application.port.in;

import java.util.List;
import java.util.Optional;

public interface CommentsManagementUseCase {

    Optional<List<CommentResult>> getComments(Long videoId);
    Optional<CommentResult> createComment(Long videoId, String email, CreateCommentCommand command);
    Optional<CommentResult> createReply(Long videoId, Long commentId, String email, CreateCommentCommand command);
}
