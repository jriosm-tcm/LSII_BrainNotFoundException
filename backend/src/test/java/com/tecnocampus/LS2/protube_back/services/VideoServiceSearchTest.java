package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class VideoServiceSearchTest {
    @TempDir Path directory;
    @Test void searchMatchesTitlesAndDescriptions() throws Exception {
        Files.write(directory.resolve("123.mp4"), new byte[]{0});
        Files.writeString(directory.resolve("123.json"),
                "{\"title\":\"Java tutorial\",\"user\":\"Alexis\",\"meta\":{\"description\":\"Spring Boot\"}}");
        VideoService service = new VideoService(directory.toString(), new ObjectMapper());
        assertEquals(1, service.searchVideos("java").size());
        assertEquals(1, service.searchVideos("spring").size());
        assertEquals(0, service.searchVideos("missing").size());
        assertEquals(1, service.searchVideos(" ").size());
    }
}
