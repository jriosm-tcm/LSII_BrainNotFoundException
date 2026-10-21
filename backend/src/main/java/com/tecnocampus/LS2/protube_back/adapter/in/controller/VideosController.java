package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideosController {

    @Autowired
    VideoService videoService;

    @GetMapping("")
    public ResponseEntity<List<String>> getVideos() {
        return ResponseEntity.ok().body(videoService.getVideos());

    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable int id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }
}
