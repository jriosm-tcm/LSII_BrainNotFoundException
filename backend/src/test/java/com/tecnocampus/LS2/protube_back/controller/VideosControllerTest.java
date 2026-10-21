package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.dtos.VideoDTO;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        VideoDTO video1 = new VideoDTO(1L, "Video 1", "Description 1", "url1", "thumbnail1");
        VideoDTO video2 = new VideoDTO(2L, "Video 2", "Description 2", "url2", "thumbnail2");

        when(videoService.getVideos()).thenReturn(List.of(video1, video2));
        assertEquals(List.of(video1, video2), videosController.getVideos().getBody());
    }
}