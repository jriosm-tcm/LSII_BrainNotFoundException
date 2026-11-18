package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class AvatarService {
    private final Path avatars;
    private final UserRepository users;

    public AvatarService(@Value("${pro_tube.store.dir}") String storeDir, UserRepository users) {
        this.avatars = Path.of(storeDir).resolve("avatars");
        this.users = users;
    }

    public String upload(String email, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 2L * 1024 * 1024)
            throw new IllegalArgumentException("Choose an image smaller than 2 MB");
        byte[] bytes = file.getBytes();
        boolean png = bytes.length >= 8 && (bytes[0] & 255) == 137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71
                && bytes[4] == 13 && bytes[5] == 10 && bytes[6] == 26 && bytes[7] == 10;
        boolean jpeg = bytes.length >= 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255;
        if (!png && !jpeg) throw new IllegalArgumentException("Only PNG and JPEG images are supported");
        var user = users.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Files.createDirectories(avatars);
        String name = UUID.randomUUID() + (png ? ".png" : ".jpg");
        Path target = avatars.resolve(name);
        Files.write(target, bytes);
        String old = user.getAvatarUrl();
        user.setAvatarUrl("/api/profile/avatar/" + name);
        try {
            users.save(user);
        } catch (RuntimeException ex) {
            Files.deleteIfExists(target);
            throw ex;
        }
        if (old != null && old.startsWith("/api/profile/avatar/")) {
            String previous = old.substring("/api/profile/avatar/".length());
            if (previous.matches("[a-f0-9-]{36}\\.(png|jpg)")) Files.deleteIfExists(avatars.resolve(previous));
        }
        return user.getAvatarUrl();
    }

    public Resource read(String name) throws IOException {
        if (!name.matches("[a-f0-9-]{36}\\.(png|jpg)")) throw new java.io.FileNotFoundException();
        Path file = avatars.resolve(name);
        if (!Files.isRegularFile(file)) throw new java.io.FileNotFoundException();
        return new UrlResource(file.toUri());
    }

    public MediaType mediaType(String name) {
        return name.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
    }
}
