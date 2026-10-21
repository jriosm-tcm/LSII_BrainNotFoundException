package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoMagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
public class VideoService implements VideoMagementUseCase {

    private final VideoPort videoPort;

    public VideoService(VideoPort videoPort) {
        this.videoPort = videoPort;
    }

    public List<String> getVideos() {
        return videoPort.getVideos();
    }
}
