package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.services.VideoService;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideosController {

    private final VideoManagementUseCase videoUseCase;

    public VideosController(VideoManagementUseCase videoUseCase) {
        this.videoUseCase = videoUseCase;
    }

    @GetMapping
    public ResponseEntity<List<VideoDTO>> getVideos() {
        List<VideoDTO> videos = videoUseCase.getVideos()
                .stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long id) {
        Video video = videoUseCase.getVideoById(id);
        return ResponseEntity.ok(toDto(video));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam(name = "q", defaultValue = "") String query) {
        List<VideoDTO> videos = videoUseCase.searchVideos(query)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(videos);
    }

    private VideoDTO toDto(Video video) {
        return new VideoDTO(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getUrl(),
                video.getThumbnailUrl()
        );
    }
}
