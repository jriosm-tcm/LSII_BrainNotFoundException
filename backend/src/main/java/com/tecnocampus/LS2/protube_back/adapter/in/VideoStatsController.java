package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsResult;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos/{videoId}/stats")
public class VideoStatsController {

    private final VideoStatsUseCase statsUseCase;

    public VideoStatsController(VideoStatsUseCase statsUseCase) {
        this.statsUseCase = statsUseCase;
    }

    @GetMapping
    public ResponseEntity<VideoStatsResult> getStats(@PathVariable("videoId") Long videoId) {
        return ResponseEntity.of(statsUseCase.getStats(videoId));
    }

    @PostMapping("/view")
    public ResponseEntity<VideoStatsResult> registerView(@PathVariable("videoId") Long videoId) {
        return ResponseEntity.of(statsUseCase.registerView(videoId));
    }
}
