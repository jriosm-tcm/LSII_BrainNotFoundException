package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoUploadUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoStoragePort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class VideoUploadService implements VideoUploadUseCase {

    private final UsersPort usersPort;
    private final VideoPort videoPort;
    private final VideoStoragePort videoStorage;

    public VideoUploadService(
            UsersPort usersPort,
            VideoPort videoPort,
            VideoStoragePort videoStorage
    ) {
        this.usersPort = usersPort;
        this.videoPort = videoPort;
        this.videoStorage = videoStorage;
    }

    @Override
    public Video uploadVideo(
            String email,
            String title,
            String description,
            InputStream content
    ) {
        if (title == null || title.isBlank() || title.length() > 200) {
            throw new IllegalArgumentException(
                    "El título es obligatorio y admite hasta 200 caracteres."
            );
        }

        if (description != null && description.length() > 5000) {
            throw new IllegalArgumentException(
                    "La descripción admite hasta 5000 caracteres."
            );
        }

        Users owner = usersPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el usuario autenticado."
                ));

        String videoUrl = videoStorage.save(content);

        try {
            Video video = new Video(
                    null,
                    title.trim(),
                    description == null ? "" : description.trim(),
                    videoUrl,
                    ""
            );

            return videoPort.saveVideo(video, owner.getId());
        } catch (RuntimeException exception) {
            videoStorage.delete(videoUrl);
            throw exception;
        }
    }
}
