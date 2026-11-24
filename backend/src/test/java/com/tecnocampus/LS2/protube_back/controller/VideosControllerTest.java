package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideosControllerTest {
    @InjectMocks
    VideosController videosController;

    @Mock
    VideoService videoService;

    @Test
    void getVideos() {
        List<Map<String, Object>> videos = List.of(
                Map.of("id", "1", "title", "video 1"),
                Map.of("id", "2", "title", "video 2"));
        when(videoService.getVideos()).thenReturn(videos);
        assertEquals(videos, videosController.getVideos().getBody());
    }
}
