package com.tecnocampus.LS2.protube_back.application.port.in;

import java.util.Optional;

public interface ProfileManagementUseCase {

    int MAX_AVATAR_BYTES = 2 * 1024 * 1024;

    Optional<ProfileResult> getMyProfile(String email);

    Optional<ProfileResult> updateMyProfile(
            String email,
            UpdateProfileCommand command
    );

    Optional<ProfileResult> uploadMyAvatar(
            String email,
            byte[] content
    );
}
