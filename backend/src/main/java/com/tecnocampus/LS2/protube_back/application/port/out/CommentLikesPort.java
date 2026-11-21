package com.tecnocampus.LS2.protube_back.application.port.out;

public interface CommentLikesPort {

    long countLikes(Long commentId);
    boolean existsLike(Long commentId, String userId);
    void addLike(Long commentId, String userId);
    void removeLike(Long commentId, String userId);
}
