package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.application.port.out.CommentLikesPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class CommentLikesRepository implements CommentLikesPort {

    private final JdbcClient jdbcClient;

    public CommentLikesRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public long countLikes(Long commentId) {
        return jdbcClient.sql("""
            SELECT COUNT(*)
            FROM comment_likes
            WHERE comment_id = :commentId
            """)
                .param("commentId", commentId)
                .query(Long.class)
                .single();
    }

    @Override
    public boolean existsLike(Long commentId, String userId) {
        return jdbcClient.sql("""
            SELECT COUNT(*)
            FROM comment_likes
            WHERE comment_id = :commentId
              AND user_id = :userId
            """)
                .param("commentId", commentId)
                .param("userId", userId)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public void addLike(Long commentId, String userId) {
        jdbcClient.sql("""
            INSERT INTO comment_likes (comment_id, user_id)
            VALUES (:commentId, :userId)
            """)
                .param("commentId", commentId)
                .param("userId", userId)
                .update();
    }

    @Override
    public void removeLike(Long commentId, String userId) {
        jdbcClient.sql("""
            DELETE FROM comment_likes
            WHERE comment_id = :commentId
              AND user_id = :userId
            """)
                .param("commentId", commentId)
                .param("userId", userId)
                .update();
    }
}
