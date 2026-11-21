package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsResult;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.VideoStatsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VideoStatsService implements VideoStatsUseCase {

    private final VideoStatsPort statsPort;

    public VideoStatsService(VideoStatsPort statsPort) {
        this.statsPort = statsPort;
    }

    @Override
    public Optional<VideoStatsResult> getStats(Long videoId) {
        return statsPort.getStats(videoId);
    }

    @Override
    @Transactional
    public Optional<VideoStatsResult> registerView(Long videoId) {
        boolean updated = statsPort.incrementViews(videoId);

        if (!updated) {
            return Optional.empty();
        }

        return statsPort.getStats(videoId);
    }
}
