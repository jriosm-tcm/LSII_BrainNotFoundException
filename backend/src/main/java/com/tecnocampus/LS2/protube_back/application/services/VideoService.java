package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService implements VideoManagementUseCase {

    private final VideoPort videoPort;

    public VideoService(VideoPort videoPort) {
        this.videoPort = videoPort;
    }

    @Override
    public List<Video> getVideos() {
        return videoPort.getVideos();
    }

    @Override
    public Video getVideoById(Long id) {
        return videoPort.getVideoById(id)
                .orElseThrow();
    }
}
