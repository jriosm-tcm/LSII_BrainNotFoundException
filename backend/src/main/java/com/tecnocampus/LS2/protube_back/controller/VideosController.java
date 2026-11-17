package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import java.io.IOException;
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

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file,
                                    @RequestPart("title") String title,
                                    @RequestPart(value = "description", required = false) String description,
                                    Authentication authentication) throws IOException {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(videoService.uploadVideo(file, title, description, authentication.getName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

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
