package com.tecnocampus.LS2.protube_back.application.port.in;

import com.tecnocampus.LS2.protube_back.domain.model.Video;

import java.io.InputStream;

public interface VideoUploadUseCase {

    long MAX_VIDEO_BYTES = 100L * 1024 * 1024;

    Video uploadVideo(
            String email,
            String title,
            String description,
            InputStream content
    );
}