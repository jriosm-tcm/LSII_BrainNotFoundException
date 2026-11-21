package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsResult;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoStatsPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VideoStatsRepository implements VideoStatsPort {

    private final JdbcClient jdbcClient;

    public VideoStatsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<VideoStatsResult> getStats(Long videoId) {
        return jdbcClient.sql("""
            SELECT v.views,
                   (
                       SELECT COUNT(*)
                       FROM video_comments c
                       WHERE c.video_id = v.id
                   ) AS comment_count
            FROM videos v
            WHERE v.id = :videoId
            """)
                .param("videoId", videoId)
                .query((rs, rowNum) -> new VideoStatsResult(
                        rs.getLong("views"),
                        rs.getLong("comment_count")
                ))
                .optional();
    }

    @Override
    public boolean incrementViews(Long videoId) {
        return jdbcClient.sql("""
            UPDATE videos
            SET views = views + 1
            WHERE id = :videoId
            """)
                .param("videoId", videoId)
                .update() == 1;
    }
}
