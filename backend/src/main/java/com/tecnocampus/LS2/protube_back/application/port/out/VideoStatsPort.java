package com.tecnocampus.LS2.protube_back.application.port.out;

import com.tecnocampus.LS2.protube_back.application.port.in.VideoStatsResult;

import java.util.Optional;

public interface VideoStatsPort {

    Optional<VideoStatsResult> getStats(Long videoId);
    boolean incrementViews(Long videoId);
}
