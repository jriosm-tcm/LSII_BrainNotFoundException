package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.access.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class VideoMetadataTest {
    @TempDir Path directory;

    @Test void onlyOwnerCanEditVideo() throws Exception {
        Files.write(directory.resolve("123.mp4"), new byte[]{0});
        Files.writeString(directory.resolve("123.json"),
                "{\"title\":\"Original\",\"user\":\"owner@example.com\",\"meta\":{\"description\":\"old\"}}");
        VideoService service = new VideoService(directory.toString(), new ObjectMapper());
        assertThrows(AccessDeniedException.class, () ->
                service.updateMetadata("123", "other@example.com", "Changed", "new"));
        assertEquals("Original", service.getVideos().get(0).get("title"));
        service.updateMetadata("123", "owner@example.com", "Changed", "new");
        assertEquals("Changed", service.getVideos().get(0).get("title"));
        assertEquals("new", service.getVideos().get(0).get("description"));
    }
}
