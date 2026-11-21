package com.tecnocampus.LS2.protube_back.application.port.in;

import java.util.Optional;

public interface CommentLikesUseCase {

    Optional<CommentLikesResult> getLikes(Long videoId, Long commentId);

    Optional<CommentLikeResult> toggleLike(Long videoId, Long commentId, String email);
}
