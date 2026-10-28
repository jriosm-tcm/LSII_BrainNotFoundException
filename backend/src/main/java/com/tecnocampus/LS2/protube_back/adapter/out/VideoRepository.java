package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

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
                .sql("SELECT id, title, description FROM videos WHERE id = :id")
                .param("id", id)
                .query(videoMapper)
                .optional();
    }

    @Override
    public List<Video> getVideos() {
        return jdbcClient
                .sql("SELECT id, title, description FROM videos")
                .query(videoMapper)
                .list();
    }

    private final RowMapper<Video> videoMapper = (rs, rowNum) -> {
        Long id = rs.getLong("id");

        return new Video(
                id,
                rs.getString("title"),
                rs.getString("description"),
                "/" + id + ".mp4",
                "/" + id + ".webp"
        );
    };


}


