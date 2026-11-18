package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.users.AppUser;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ProfileService {
    private final UserRepository users;

    public ProfileService(UserRepository users) {
        this.users = users;
    }

    public record Profile(String email, String description, String avatarUrl) {
        public static Profile from(AppUser user) {
            return new Profile(user.getEmail(), user.getDescription(), user.getAvatarUrl());
        }
    }

    @Transactional(readOnly = true)
    public Optional<Profile> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email).map(Profile::from);
    }

    @Transactional
    public Optional<Profile> update(String email, String description, String avatarUrl) {
        return users.findByEmailIgnoreCase(email).map(user -> {
            user.setDescription(description == null ? "" : description);
            user.setAvatarUrl(avatarUrl == null ? "" : avatarUrl);
            return Profile.from(users.save(user));
        });
    }
}
