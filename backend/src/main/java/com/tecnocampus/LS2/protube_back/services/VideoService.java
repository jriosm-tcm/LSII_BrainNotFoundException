package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dtos.VideoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final JdbcTemplate jdbcTemplate;

    public VideoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public VideoDTO getVideoById(int id) {
        String sql = "SELECT id, title, description FROM videos WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new VideoDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        (rs.getString("id") + ".mp4"),
                        (rs.getString("id") + ".webp")
                )
        );
    }

    public List<VideoDTO> getVideos() {
        String sql = "SELECT id, title, description FROM videos";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new VideoDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        (rs.getString("id") + ".mp4"),
                        (rs.getString("id") + ".webp")
                )
        );
    }
}