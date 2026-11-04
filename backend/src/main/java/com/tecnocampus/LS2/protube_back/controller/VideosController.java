package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideosController {

    @Autowired
    VideoService videoService;

    @GetMapping("/search")
    public ResponseEntity<List<java.util.Map<String, Object>>> searchVideos(
            @RequestParam(name = "q", defaultValue = "") String query) {
        return ResponseEntity.ok(videoService.searchVideos(query));
    }

    @GetMapping("")
    public ResponseEntity<List<java.util.Map<String, Object>>> getVideos() {
        return ResponseEntity.ok().body(videoService.getVideos());

    }
}
