package com.tecnocampus.LS2.protube_back.application.port.out;

import com.tecnocampus.LS2.protube_back.domain.model.Comments;

import java.util.List;

public interface CommentsPort {

    List<Comments> getCommentsByVideoId(Long videoId);
    Comments saveComment(Comments comment);
}
