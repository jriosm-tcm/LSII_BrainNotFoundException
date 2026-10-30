package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.application.services.VideoService;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoPort videoPort;

    private VideoService videoService;

    @BeforeEach
    void setUp() {
        videoService = new VideoService(videoPort);
    }

    @Test
    void returnsTheVideosProvidedByTheOutputPort() {
        Video video = new Video(0L, "Primer vídeo", "Descripción", "/0.mp4", "/0.webp");
        when(videoPort.getVideos()).thenReturn(List.of(video));

        assertThat(videoService.getVideos()).containsExactly(video);
    }

    @Test
    void returnsAnEmptyListWhenThereAreNoVideos() {
        when(videoPort.getVideos()).thenReturn(List.of());

        assertThat(videoService.getVideos()).isEmpty();
    }

    @Test
    void findsAVideoWithoutNarrowingItsLongIdentifier() {
        long id = 3_000_000_000L;
        Video video = new Video(id, "Vídeo", null, "/video.mp4", "/thumbnail.webp");
        when(videoPort.getVideoById(id)).thenReturn(Optional.of(video));

        assertThat(videoService.getVideoById(id)).isSameAs(video);
        verify(videoPort).getVideoById(id);
    }

    @Test
    void throwsWhenTheVideoDoesNotExist() {
        when(videoPort.getVideoById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.getVideoById(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void doesNotDisguiseADatabaseFailureAsAnEmptyCatalog() {
        var failure = new DataAccessResourceFailureException("Database unavailable");
        when(videoPort.getVideos()).thenThrow(failure);

        assertThatThrownBy(() -> videoService.getVideos()).isSameAs(failure);
    }
}
