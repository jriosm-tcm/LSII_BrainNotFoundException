package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikeResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikesResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentLikesUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.CommentLikesPort;
import com.tecnocampus.LS2.protube_back.application.port.out.CommentsPort;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentLikesService implements CommentLikesUseCase {

    private final CommentsPort commentsPort;
    private final UsersPort usersPort;
    private final CommentLikesPort likesPort;

    public CommentLikesService(CommentsPort commentsPort, UsersPort usersPort, CommentLikesPort likesPort) {
        this.commentsPort = commentsPort;
        this.usersPort = usersPort;
        this.likesPort = likesPort;
    }

    @Override
    public Optional<CommentLikesResult> getLikes(Long videoId, Long commentId) {
        if (commentsPort.getCommentById(videoId, commentId).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new CommentLikesResult(likesPort.countLikes(commentId)));
    }

    @Override
    @Transactional
    public Optional<CommentLikeResult> toggleLike(Long videoId, Long commentId, String email) {
        if (!commentsPort.lockComment(videoId, commentId)) {
            return Optional.empty();
        }

        Optional<Users> userResult = usersPort.findByEmail(email);

        if (userResult.isEmpty()) {
            return Optional.empty();
        }

        String userId = userResult.get().getId();

        boolean liked = !likesPort.existsLike(commentId, userId);

        if (liked) {
            likesPort.addLike(commentId, userId);
        } else {
            likesPort.removeLike(commentId, userId);
        }

        return Optional.of(new CommentLikeResult(likesPort.countLikes(commentId), liked));
    }
}
