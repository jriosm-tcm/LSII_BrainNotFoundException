package com.tecnocampus.LS2.protube_back.application.port.in;

import java.util.Optional;

public interface VideoStatsUseCase {

    Optional<VideoStatsResult> getStats(Long videoId);
    Optional<VideoStatsResult> registerView(Long videoId);
}
