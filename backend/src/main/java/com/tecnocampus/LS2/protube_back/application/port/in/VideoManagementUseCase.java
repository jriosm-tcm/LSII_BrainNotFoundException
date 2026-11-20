package com.tecnocampus.LS2.protube_back.application.port.in;

import com.tecnocampus.LS2.protube_back.domain.model.Video;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VideoManagementUseCase {

    Video getVideoById(Long id);
    List<Video> getVideos();
    List<Video> searchVideos(String query);
    List<Video> getMyVideos(String email);
    Optional<Video> updateMetadata(Long id, String email, UpdateVideoMetadataCommand command);
}
