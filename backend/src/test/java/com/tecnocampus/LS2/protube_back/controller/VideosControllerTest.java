package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.adapter.in.controller.VideosController;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VideosControllerTest {

    @Mock
    private VideoManagementUseCase videoUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VideosController(videoUseCase)).build();
    }

    @Test
    void returnsTheCatalogWithThePublicDtoFields() throws Exception {
        when(videoUseCase.getVideos()).thenReturn(List.of(
                new Video(0L, "Primer vídeo", "Descripción", "/0.mp4", "/0.webp"),
                new Video(7L, "Segundo vídeo", "Otra descripción", "/7.mp4", "/7.webp")
        ));

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [
                          {"id":0,"title":"Primer vídeo","description":"Descripción",
                           "url":"/0.mp4","thumbnailUrl":"/0.webp"},
                          {"id":7,"title":"Segundo vídeo","description":"Otra descripción",
                           "url":"/7.mp4","thumbnailUrl":"/7.webp"}
                        ]
                        """, true));
    }

    @Test
    void returnsAnEmptyJsonArrayForAnEmptyCatalog() throws Exception {
        when(videoUseCase.getVideos()).thenReturn(List.of());

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]", true));
    }

    @Test
    void bindsALongIdentifierAndReturnsTheVideoDto() throws Exception {
        long id = 3_000_000_000L;
        when(videoUseCase.getVideoById(id)).thenReturn(
                new Video(id, "Detalle", "Descripción", "/clip.mp4", "/cover.webp")
        );

        mockMvc.perform(get("/api/videos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":3000000000,"title":"Detalle","description":"Descripción",
                         "url":"/clip.mp4","thumbnailUrl":"/cover.webp"}
                        """, true));

        verify(videoUseCase).getVideoById(id);
    }

    @Test
    void rejectsANonNumericIdentifierBeforeCallingTheUseCase() throws Exception {
        mockMvc.perform(get("/api/videos/not-a-number"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(videoUseCase);
    }

    @Test
    void serializesAVideoWithANullDescription() throws Exception {
        when(videoUseCase.getVideoById(0L)).thenReturn(
                new Video(0L, "Sin descripción", null, "/0.mp4", "/0.webp")
        );

        mockMvc.perform(get("/api/videos/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(content().json("""
                        {"id":0,"title":"Sin descripción","description":null,
                         "url":"/0.mp4","thumbnailUrl":"/0.webp"}
                        """, true));
    }
}
