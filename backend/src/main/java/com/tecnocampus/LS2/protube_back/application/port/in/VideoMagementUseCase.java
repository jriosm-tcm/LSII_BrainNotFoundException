package com.tecnocampus.LS2.protube_back.application.port.in;

import com.tecnocampus.LS2.protube_back.application.model.Video;

public interface VideoMagementUseCase {

    Video getVideoById(Long id);
}
