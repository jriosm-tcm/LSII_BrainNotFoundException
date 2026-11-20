package com.tecnocampus.LS2.protube_back.adapter.out.storage;

import com.tecnocampus.LS2.protube_back.application.port.in.ProfileManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.AvatarStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.UUID;

@Component
public class FileSystemAvatarAdapter implements AvatarStoragePort {

    private static final Logger LOG =
            LoggerFactory.getLogger(FileSystemAvatarAdapter.class);

    private static final String PUBLIC_PREFIX = "/media/avatars/";
    private static final int MAX_IMAGE_SIDE = 4096;

    private final Path avatarsDirectory;

    public FileSystemAvatarAdapter(
            @Value("${pro_tube.store.dir}") String storeDir
    ) {
        this.avatarsDirectory = Path.of(storeDir)
                .toAbsolutePath()
                .normalize()
                .resolve("avatars");
    }

    @Override
    public String save(byte[] content) {
        if (content == null
                || content.length == 0
                || content.length > ProfileManagementUseCase.MAX_AVATAR_BYTES) {
            throw new IllegalArgumentException(
                    "Selecciona una imagen de hasta 2 MB."
            );
        }

        String extension = validateImage(content);
        String filename = UUID.randomUUID() + "." + extension;
        Path target = avatarsDirectory.resolve(filename);

        boolean created = false;

        try {
            Files.createDirectories(avatarsDirectory);

            try (OutputStream output = Files.newOutputStream(
                    target,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            )) {
                created = true;
                output.write(content);
            }

            return PUBLIC_PREFIX + filename;
        } catch (IOException exception) {
            if (created) {
                deleteQuietly(target);
            }

            LOG.error("No se ha podido guardar el avatar.", exception);

            throw new UncheckedIOException(
                    "No se ha podido guardar el avatar.",
                    exception
            );
        }
    }

    @Override
    public void delete(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        String filename = avatarUrl.substring(PUBLIC_PREFIX.length());

        if (!filename.matches("[0-9a-f-]{36}\\.(png|jpg)")) {
            return;
        }

        deleteQuietly(avatarsDirectory.resolve(filename));
    }

    private String validateImage(byte[] content) {
        try (ImageInputStream input = ImageIO.createImageInputStream(
                new ByteArrayInputStream(content)
        )) {
            if (input == null) {
                throw new IllegalArgumentException(
                        "No se ha podido leer la imagen."
                );
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException(
                        "Solo se permiten imágenes PNG o JPEG."
                );
            }

            ImageReader reader = readers.next();

            try {
                String format = reader.getFormatName();

                boolean png = format.equalsIgnoreCase("png");
                boolean jpeg = format.equalsIgnoreCase("jpeg")
                        || format.equalsIgnoreCase("jpg");

                if (!png && !jpeg) {
                    throw new IllegalArgumentException(
                            "Solo se permiten imágenes PNG o JPEG."
                    );
                }

                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0
                        || height <= 0
                        || width > MAX_IMAGE_SIDE
                        || height > MAX_IMAGE_SIDE) {
                    throw new IllegalArgumentException(
                            "La imagen admite hasta 4096 píxeles por lado."
                    );
                }

                if (reader.read(0) == null) {
                    throw new IllegalArgumentException(
                            "El archivo no contiene una imagen válida."
                    );
                }

                return png ? "png" : "jpg";
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "No se ha podido leer la imagen PNG o JPEG.",
                    exception
            );
        }
    }

    private void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            LOG.warn(
                    "No se ha podido eliminar el avatar {}",
                    file,
                    exception
            );
        }
    }
}