package com.tecnocampus.LS2.protube_back.adapter.out.security;

import com.tecnocampus.LS2.protube_back.application.port.out.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SpringPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordHasher(PasswordEncoder passwordEndcoder) {
        this.passwordEncoder = passwordEndcoder;
    }

    @Override
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }
    @Override
    public boolean matches(String password, String storedHash) {
        return passwordEncoder.matches(password, storedHash);
    }
}
