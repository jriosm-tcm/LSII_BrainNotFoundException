package com.tecnocampus.LS2.protube_back.application.port.out;

import com.tecnocampus.LS2.protube_back.domain.model.Comments;

import java.util.List;
import java.util.Optional;

public interface CommentsPort {

    List<Comments> getCommentsByVideoId(Long videoId);
    Comments saveComment(Comments comment);
    Optional<Comments> getCommentById(Long videoId, Long commentId);
}
