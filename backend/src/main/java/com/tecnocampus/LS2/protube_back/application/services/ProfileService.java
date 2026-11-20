package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.application.port.in.ProfileManagementUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.ProfileResult;
import com.tecnocampus.LS2.protube_back.application.port.in.PublicProfileResult;
import com.tecnocampus.LS2.protube_back.application.port.in.UpdateProfileCommand;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.stereotype.Service;
import com.tecnocampus.LS2.protube_back.application.port.out.AvatarStoragePort;

import java.util.Optional;

@Service
public class ProfileService implements ProfileManagementUseCase {

    private final UsersPort usersPort;
    private final AvatarStoragePort avatarStorage;

    public ProfileService(UsersPort usersPort, AvatarStoragePort avatarStorage) {
        this.usersPort = usersPort;
        this.avatarStorage = avatarStorage;
    }

    @Override
    public Optional<ProfileResult> getMyProfile(String email) {
        return usersPort.findByEmail(email)
                .map(this::toResult);
    }

    @Override
    public Optional<ProfileResult> updateMyProfile(
            String email,
            UpdateProfileCommand command
    ) {
        Optional<Users> result = usersPort.findByEmail(email);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Users user = result.get();

        boolean updated = usersPort.updateDescription(
                user.getId(),
                command.description()
        );

        if (!updated) {
            return Optional.empty();
        }

        user.setDescription(command.description());

        return Optional.of(toResult(user));
    }

    @Override
    public Optional<ProfileResult> uploadMyAvatar(
            String email,
            byte[] content
    ) {
        Optional<Users> result = usersPort.findByEmail(email);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        Users user = result.get();

        String previousAvatarUrl = user.getAvatarUrl();
        String newAvatarUrl = avatarStorage.save(content);

        try {
            boolean updated = usersPort.updateAvatarUrl(
                    user.getId(),
                    newAvatarUrl
            );

            if (!updated) {
                avatarStorage.delete(newAvatarUrl);
                return Optional.empty();
            }
        } catch (RuntimeException exception) {
            avatarStorage.delete(newAvatarUrl);
            throw exception;
        }

        user.setAvatarUrl(newAvatarUrl);

        avatarStorage.delete(previousAvatarUrl);

        return Optional.of(toResult(user));
    }

    private ProfileResult toResult(Users user) {
        return new ProfileResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDescription(),
                user.getAvatarUrl()
        );
    }

    @Override
    public Optional<PublicProfileResult> getPublicProfile(String id) {
        return usersPort.findById(id)
                .map(user -> new PublicProfileResult(
                        user.getId(),
                        user.getUsername(),
                        user.getDescription(),
                        user.getAvatarUrl()
                ));
    }
}
