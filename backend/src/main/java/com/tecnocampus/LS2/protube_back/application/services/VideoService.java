package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoPort;
import com.tecnocampus.LS2.protube_back.domain.model.Video;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class VideoService implements VideoManagementUseCase {

    private final VideoPort videoPort;

    public VideoService(VideoPort videoPort) {
        this.videoPort = videoPort;
    }

    @Override
    public List<Video> getVideos() {
        return videoPort.getVideos();
    }

    @Override
    public Video getVideoById(Long id) {
        return videoPort.getVideoById(id)
                .orElseThrow();
    }

    @Override
    public List<Video> searchVideos(String query) {
        String term = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        if (term.isEmpty()) {
            return getVideos();
        }

        return getVideos().stream()
                .map(video -> new SearchMatch(video, relevance(video, term)))
                .filter(match -> match.score() > 0)
                .sorted(Comparator.comparingInt(SearchMatch::score)
                        .reversed()
                        .thenComparing(match -> match.video().getTitle(),
                                String.CASE_INSENSITIVE_ORDER))
                .map(SearchMatch::video)
                .toList();
    }

    private int relevance(Video video, String term) {
        String title = video.getTitle().toLowerCase(Locale.ROOT);
        String description = video.getDescription();

        if (title.equals(term)) {
            return 4;
        }
        if (title.startsWith(term)) {
            return 3;
        }
        if (title.contains(term)) {
            return 2;
        }
        if (description != null && description.toLowerCase(Locale.ROOT).contains(term)) {
            return 1;
        }
        return 0;
    }

    private record SearchMatch(Video video, int score) {}
}
