package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.users.AppUser;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public Optional<String> register(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalizedEmail)) return Optional.empty();
        try {
            users.saveAndFlush(new AppUser(normalizedEmail, encoder.encode(password)));
            return Optional.of(normalizedEmail);
        } catch (DataIntegrityViolationException duplicateEmail) {
            return Optional.empty();
        }
    }
}
