package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {
    private final Path store;
    private final ObjectMapper mapper;

    public VideoService(@Value("${pro_tube.store.dir}") String storeDir, ObjectMapper mapper) {
        this.store = Path.of(storeDir);
        this.mapper = mapper;
    }

    public List<Map<String, Object>> getVideos() {
        List<Map<String, Object>> videos = new ArrayList<>();
        if (!Files.isDirectory(store)) return videos;
        try (var files = Files.list(store)) {
            for (Path file : files.filter(p -> p.getFileName().toString().matches("[0-9]+\\.json"))
                    .sorted(Comparator.comparing(Path::toString)).toList()) {
                try {
                    String id = file.getFileName().toString().replace(".json", "");
                    if (!Files.isRegularFile(store.resolve(id + ".mp4"))) continue;
                    JsonNode info = mapper.readTree(file.toFile());
                    videos.add(Map.of(
                            "id", id,
                            "title", info.path("title").asText("Untitled video"),
                            "description", info.path("meta").path("description").asText(""),
                            "channel", info.path("user").asText("Unknown"),
                            "videoUrl", "/media/" + id + ".mp4",
                            "thumbnailUrl", Files.exists(store.resolve(id + ".webp")) ? "/media/" + id + ".webp" : ""
                    ));
                } catch (IOException ignored) {
                    // Skip invalid metadata; one broken entry must not hide other videos.
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read video store: " + store, e);
        }
        return videos;
    }
}
