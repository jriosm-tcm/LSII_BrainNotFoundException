package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.application.port.out.CommentsPort;
import com.tecnocampus.LS2.protube_back.domain.model.Comments;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentsRepository implements CommentsPort {

    private final JdbcClient jdbcClient;

    public CommentsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Comments> getCommentsByVideoId(Long videoId) {
        return jdbcClient.sql("""
            SELECT c.id,
                   c.video_id,
                   c.parent_id,
                   c.author_id,
                   u.username AS author_name,
                   c.content,
                   c.created_at
            FROM video_comments c
            JOIN users u ON c.author_id = u.id
            WHERE c.video_id = :videoId
            ORDER BY c.created_at ASC, c.id ASC
            """)
                .param("videoId", videoId)
                .query(commentMapper)
                .list();
    }

    @Override
    public Optional<Comments> getCommentById(Long videoId, Long commentId) {
        return jdbcClient.sql("""
            SELECT c.id,
                   c.video_id,
                   c.parent_id,
                   c.author_id,
                   u.username AS author_name,
                   c.content,
                   c.created_at
            FROM video_comments c
            JOIN users u ON c.author_id = u.id
            WHERE c.id = :commentId
              AND c.video_id = :videoId
            """)
                .param("commentId", commentId)
                .param("videoId", videoId)
                .query(commentMapper)
                .optional();
    }

    @Override
    public Comments saveComment(Comments comment) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
            INSERT INTO video_comments (
                video_id,
                parent_id,
                author_id,
                content,
                created_at
            )
            VALUES (
                :videoId,
                :parentId,
                :authorId,
                :content,
                :createdAt
            )
            """)
                .param("videoId", comment.getVideoId())
                .param("parentId", comment.getParentId())
                .param("authorId", comment.getAuthorId())
                .param("content", comment.getContent())
                .param("createdAt", comment.getCreatedAt())
                .update(keyHolder, "id");

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "No se ha podido obtener el identificador del comentario."
            );
        }

        return new Comments(
                generatedId.longValue(),
                comment.getVideoId(),
                comment.getParentId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    private final RowMapper<Comments> commentMapper = (rs, rowNum) ->
            new Comments(
                    rs.getLong("id"),
                    rs.getLong("video_id"),
                    rs.getObject("parent_id", Long.class),
                    rs.getString("author_id"),
                    rs.getString("author_name"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
}
