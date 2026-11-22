package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.List;
import java.util.Optional;

@Repository
public class VideoRepository implements VideoPort {
    private final JdbcClient jdbcClient;

    public VideoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Video> getVideoById(Long id) {
        return jdbcClient
                .sql("""
                    SELECT v.id,
                        v.title,
                        v.description,
                        v.video_url,
                        v.thumbnail_url,
                        u.username AS author_name
                    FROM videos v
                    LEFT JOIN users u ON v.owner_id = u.id
                    WHERE v.id = :id
                    """)
                .param("id", id)
                .query(videoMapper)
                .optional();
    }

    @Override
    public List<Video> getVideos() {
        return jdbcClient
                .sql("""
                    SELECT v.id,
                        v.title,
                        v.description,
                        v.video_url,
                        v.thumbnail_url,
                        u.username AS author_name
                    FROM videos v
                    LEFT JOIN users u ON v.owner_id = u.id
                    """)
                .query(videoMapper)
                .list();
    }

    private final RowMapper<Video> videoMapper = (rs, rowNum) -> {
        Long id = rs.getLong("id");

        String videoUrl = rs.getString("video_url");
        String thumbnailUrl = rs.getString("thumbnail_url");

        return new Video(
                id,
                rs.getString("title"),
                rs.getString("description"),
                videoUrl == null ? "/" + id + ".mp4" : videoUrl,
                thumbnailUrl == null ? "/" + id + ".webp" : thumbnailUrl,
                rs.getString("author_name")
        );
    };

    @Override
    public List<Video> getVideosByOwner(String email) {
        return jdbcClient
                .sql("""
                    SELECT v.id,
                        v.title,
                        v.description,
                        v.video_url,
                        v.thumbnail_url,
                        u.username AS author_name
                    FROM videos v
                    JOIN users u ON v.owner_id = u.id
                    WHERE u.email = :email
                    ORDER BY v.id DESC
                    """)
                .param("email", email)
                .query(videoMapper)
                .list();
    }

    @Override
    public Video saveVideo(Video video, String ownerId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("""
        INSERT INTO videos (
            title,
            description,
            video_url,
            thumbnail_url,
            owner_id
        )
        VALUES (
            :title,
            :description,
            :videoUrl,
            :thumbnailUrl,
            :ownerId
        )
        """)
                .param("title", video.getTitle())
                .param("description", video.getDescription())
                .param("videoUrl", video.getUrl())
                .param("thumbnailUrl", video.getThumbnailUrl())
                .param("ownerId", ownerId)
                .update(keyHolder, "id");

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "No se ha podido obtener el identificador del vídeo."
            );
        }

        return new Video(
                generatedId.longValue(),
                video.getTitle(),
                video.getDescription(),
                video.getUrl(),
                video.getThumbnailUrl(),
                video.getAuthorName()
        );
    }

    @Override
    public boolean updateMetadata(
            Long id,
            String ownerEmail,
            String title,
            String description
    ) {
        return jdbcClient.sql("""
        UPDATE videos
        SET title = :title,
            description = :description
        WHERE id = :id
          AND owner_id = (
              SELECT id
              FROM users
              WHERE email = :ownerEmail
          )
        """)
                .param("title", title)
                .param("description", description)
                .param("id", id)
                .param("ownerEmail", ownerEmail)
                .update() == 1;
    }

}


