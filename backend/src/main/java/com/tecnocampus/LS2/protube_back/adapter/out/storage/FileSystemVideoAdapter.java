package com.tecnocampus.LS2.protube_back.adapter.out.storage;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoUploadUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
public class FileSystemVideoAdapter implements VideoStoragePort {

    private static final Logger LOG =
            LoggerFactory.getLogger(FileSystemVideoAdapter.class);

    private static final String PUBLIC_PREFIX = "/uploads/";

    private final Path uploadsDirectory;

    public FileSystemVideoAdapter(
            @Value("${pro_tube.store.dir}") String storeDir
    ) {
        this.uploadsDirectory = Path.of(storeDir)
                .toAbsolutePath()
                .normalize()
                .resolve("uploads");
    }

    @Override
    public String save(InputStream content) {
        String filename = UUID.randomUUID() + ".mp4";
        Path target = uploadsDirectory.resolve(filename);

        boolean created = false;

        try {
            byte[] header = content.readNBytes(12);

            if (header.length < 12
                    || header[4] != 'f'
                    || header[5] != 't'
                    || header[6] != 'y'
                    || header[7] != 'p') {
                throw new IllegalArgumentException(
                        "Solo se permiten archivos MP4."
                );
            }

            Files.createDirectories(uploadsDirectory);

            try (OutputStream output = Files.newOutputStream(
                    target,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            )) {
                created = true;

                output.write(header);

                long total = header.length;
                byte[] buffer = new byte[8192];
                int read;

                while ((read = content.read(buffer)) != -1) {
                    total += read;

                    if (total > VideoUploadUseCase.MAX_VIDEO_BYTES) {
                        throw new IllegalArgumentException(
                                "El vídeo admite hasta 100 MB."
                        );
                    }

                    output.write(buffer, 0, read);
                }
            }

            return PUBLIC_PREFIX + filename;
        } catch (IOException exception) {
            if (created) {
                deleteQuietly(target);
            }

            throw new UncheckedIOException(
                    "No se ha podido guardar el vídeo.",
                    exception
            );
        } catch (RuntimeException exception) {
            if (created) {
                deleteQuietly(target);
            }

            throw exception;
        }
    }

    @Override
    public void delete(String videoUrl) {
        if (videoUrl == null || !videoUrl.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        String filename = videoUrl.substring(PUBLIC_PREFIX.length());

        if (!filename.matches("[0-9a-f-]{36}\\.mp4")) {
            return;
        }

        deleteQuietly(uploadsDirectory.resolve(filename));
    }

    private void deleteQuietly(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException exception) {
            LOG.warn(
                    "No se ha podido eliminar el archivo {}",
                    target,
                    exception
            );
        }
    }
}
