package com.tecnocampus.LS2.protube_back.application.port.out;

import com.tecnocampus.LS2.protube_back.domain.model.Video;

import java.util.List;
import java.util.Optional;

public interface VideoPort {

    Optional<Video> getVideoById(Long id);

    List<Video> getVideos();

}
