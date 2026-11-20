package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.VideoDTO;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoUploadUseCase;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/videos")
@SecurityRequirement(name = "basicAuth")
public class VideoUploadController {

    private final VideoUploadUseCase videoUploadUseCase;

    public VideoUploadController(VideoUploadUseCase videoUploadUseCase) {
        this.videoUploadUseCase = videoUploadUseCase;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<VideoDTO> uploadVideo(@RequestPart("file") MultipartFile file, @RequestParam("title") String title, @RequestParam(
                    name = "description",
                    defaultValue = ""
            ) String description,
            Authentication authentication
    ) throws IOException {
        if (file.isEmpty()
                || file.getSize() > VideoUploadUseCase.MAX_VIDEO_BYTES) {
            throw new IllegalArgumentException(
                    "Selecciona un MP4 de hasta 100 MB."
            );
        }

        try (InputStream content = file.getInputStream()) {
            Video video = videoUploadUseCase.uploadVideo(
                    authentication.getName(),
                    title,
                    description,
                    content
            );

            VideoDTO result = new VideoDTO(
                    video.getId(),
                    video.getTitle(),
                    video.getDescription(),
                    video.getUrl(),
                    video.getThumbnailUrl()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result);
        }
    }
}
