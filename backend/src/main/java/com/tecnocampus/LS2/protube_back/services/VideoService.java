package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@Service
public class VideoService {
    private final Path store;
    private final ObjectMapper mapper;

    public VideoService(@Value("${pro_tube.store.dir}") String storeDir, ObjectMapper mapper) {
        this.store = Path.of(storeDir);
        this.mapper = mapper;
    }

    // Sprint 3 / US-08: search is executed by the backend, not the browser.
    public List<Map<String, Object>> searchVideos(String query) {
        String term = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (term.isEmpty()) return getVideos();

        return getVideos().stream()
                .map(video -> Map.entry(video, relevance(video, term)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Comparator.<Map.Entry<Map<String, Object>, Integer>>comparingInt(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(entry -> String.valueOf(entry.getKey().get("title"))))
                .map(Map.Entry::getKey)
                .toList();
    }

    private int relevance(Map<String, Object> video, String term) {
        String title = String.valueOf(video.get("title")).toLowerCase(Locale.ROOT);
        String description = String.valueOf(video.get("description")).toLowerCase(Locale.ROOT);
        String channel = String.valueOf(video.get("channel")).toLowerCase(Locale.ROOT);
        if (title.equals(term)) return 4;
        if (title.startsWith(term)) return 3;
        if (title.contains(term)) return 2;
        return description.contains(term) || channel.contains(term) ? 1 : 0;
    }

    public Map<String, Object> uploadVideo(MultipartFile file, String title, String description, String owner) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 100L * 1024 * 1024)
            throw new IllegalArgumentException("Choose an MP4 file smaller than 100 MB");
        if (title == null || title.isBlank() || title.length() > 200)
            throw new IllegalArgumentException("Title is required (maximum 200 characters)");
        if (description != null && description.length() > 5000)
            throw new IllegalArgumentException("Description is too long");
        // Check the actual MP4 container signature, not just the client-supplied filename.
        try (InputStream stream = file.getInputStream()) {
            byte[] header = stream.readNBytes(12);
            if (header.length < 12 || header[4] != 'f' || header[5] != 't'
                    || header[6] != 'y' || header[7] != 'p')
                throw new IllegalArgumentException("Only MP4 video files are supported");
        }
        Files.createDirectories(store);
        String id;
        Path videoPath;
        do {
            id = String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits()));
            videoPath = store.resolve(id + ".mp4");
        } while (Files.exists(videoPath) || Files.exists(store.resolve(id + ".json")));

        Path temp = Files.createTempFile(store, "upload-", ".tmp");
        Path metadata = store.resolve(id + ".json");
        try {
            file.transferTo(temp);
            var info = mapper.createObjectNode();
            info.put("title", title.trim());
            info.put("user", owner);
            info.putObject("meta").put("description", description == null ? "" : description.trim());
            Files.move(temp, videoPath, StandardCopyOption.ATOMIC_MOVE);
            try {
                mapper.writeValue(metadata.toFile(), info);
            } catch (IOException ex) {
                Files.deleteIfExists(videoPath);
                throw ex;
            }
            return Map.of("id", id, "title", title.trim());
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public Map<String, Object> updateMetadata(String id, String owner, String title, String description) throws IOException {
        if (id == null || !id.matches("[0-9]+")) throw new IllegalArgumentException("Invalid video ID");
        if (title == null || title.isBlank() || title.length() > 200)
            throw new IllegalArgumentException("Title is required (maximum 200 characters)");
        if (description != null && description.length() > 5000)
            throw new IllegalArgumentException("Description is too long");
        Path path = store.resolve(id + ".json");
        if (!Files.isRegularFile(path) || !Files.isRegularFile(store.resolve(id + ".mp4")))
            throw new java.io.FileNotFoundException("Video not found");
        JsonNode info = mapper.readTree(path.toFile());
        if (!owner.equalsIgnoreCase(info.path("user").asText()))
            throw new org.springframework.security.access.AccessDeniedException("Only the owner can edit this video");
        var updated = info.deepCopy();
        if (!(updated instanceof com.fasterxml.jackson.databind.node.ObjectNode object))
            throw new IllegalArgumentException("Invalid metadata");
        object.put("title", title.trim());
        var meta = object.with("meta");
        meta.put("description", description == null ? "" : description.trim());
        Path temp = Files.createTempFile(store, "metadata-", ".tmp");
        try {
            mapper.writeValue(temp.toFile(), object);
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temp);
        }
        return Map.of("id", id, "title", title.trim(), "description", description == null ? "" : description.trim());
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
