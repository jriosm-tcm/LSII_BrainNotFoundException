package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoServiceTest {
    @TempDir
    Path store;

    @Test
    void shouldGoToFolderVideos() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        VideoService videoService = new VideoService(store.toString(), mapper);
        for (String id : List.of("1", "2")) {
            mapper.writeValue(store.resolve(id + ".json").toFile(),
                    Map.of("title", "video" + id, "user", "user@example.com"));
            Files.createFile(store.resolve(id + ".mp4"));
        }
        mapper.writeValue(store.resolve("3.json").toFile(), Map.of("title", "missing media"));
        assertEquals(List.of("video1", "video2"),
                videoService.getVideos().stream().map(video -> video.get("title")).toList());
    }

    @Test
    void shouldReturnEmptyListForEmptyStore() {
        VideoService videoService = new VideoService(store.toString(), new ObjectMapper());
        assertEquals(List.of(), videoService.getVideos());
    }
}
