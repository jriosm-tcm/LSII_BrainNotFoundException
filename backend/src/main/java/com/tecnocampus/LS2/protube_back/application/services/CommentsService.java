package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.CommentResult;
import com.tecnocampus.LS2.protube_back.application.port.in.CommentsManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.CreateCommentCommand;
import com.tecnocampus.LS2.protube_back.application.port.out.CommentsPort;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Comments;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentsService implements CommentsManagementUseCase {

    private final CommentsPort commentsPort;
    private final VideoPort videoPort;
    private final UsersPort usersPort;

    public CommentsService(CommentsPort commentsPort, VideoPort videoPort, UsersPort usersPort) {
        this.commentsPort = commentsPort;
        this.videoPort = videoPort;
        this.usersPort = usersPort;
    }

    @Override
    public Optional<List<CommentResult>> getComments(Long videoId) {
        if (videoPort.getVideoById(videoId).isEmpty()) {
            return Optional.empty();
        }

        List<CommentResult> comments = commentsPort
                .getCommentsByVideoId(videoId)
                .stream()
                .map(this::toResult)
                .toList();

        return Optional.of(comments);
    }

    @Override
    public Optional<CommentResult> createComment(Long videoId, String email, CreateCommentCommand command) {
        if (videoPort.getVideoById(videoId).isEmpty()) {
            return Optional.empty();
        }

        return saveComment(videoId, null, email, command);
    }

    @Override
    public Optional<CommentResult> createReply(Long videoId, Long commentId, String email, CreateCommentCommand command) {
        if (commentsPort.getCommentById(videoId, commentId).isEmpty()) {
            return Optional.empty();
        }

        return saveComment(videoId, commentId, email, command);
    }

    private Optional<CommentResult> saveComment(
            Long videoId,
            Long parentId,
            String email,
            CreateCommentCommand command
    ) {
        Optional<Users> authorResult = usersPort.findByEmail(email);

        if (authorResult.isEmpty()) {
            return Optional.empty();
        }

        Users author = authorResult.get();

        Comments comment = new Comments(
                null,
                videoId,
                parentId,
                author.getId(),
                author.getUsername(),
                command.content().trim(),
                LocalDateTime.now()
        );

        Comments savedComment = commentsPort.saveComment(comment);

        return Optional.of(toResult(savedComment));
    }

    private CommentResult toResult(Comments comment) {
        return new CommentResult(
                comment.getId(),
                comment.getVideoId(),
                comment.getParentId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
