package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dtos.VideoDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoServiceTest {

    JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    VideoService videoService = new VideoService(jdbcTemplate);


    @Test
    void shouldGoToFolderVideos() {
        VideoDTO video1 = new VideoDTO(1L, "Video 1", "Description 1", "/1.mp4", "/1.webp");
        VideoDTO video2 = new VideoDTO(2L, "Video 2", "Description 2", "/2.mp4", "/2.webp");

        Mockito.when(jdbcTemplate.query(Mockito.anyString(), Mockito.<RowMapper<VideoDTO>>any()))
                .thenReturn(List.of(video1, video2));

        assertEquals(List.of(video1, video2), videoService.getVideos());
    }

}