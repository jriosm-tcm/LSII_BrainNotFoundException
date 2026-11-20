package com.tecnocampus.LS2.protube_back.domain.model;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

public class Users {
    private String id;
    private String username;
    private String email;
    private String passwordhash;
    private LocalDateTime createdAt;
    private String description = "";
    private String avatarUrl = "";

    public Users(String id, String user, String email, String passwordhash, LocalDateTime createdAt) {
        this.id = id;
        this.username = user;
        this.email = email;
        this.passwordhash = passwordhash;
        this.createdAt = createdAt;
    }

    public static Users createUser(String username, String email, String passwordhash) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return new Users(UUID.randomUUID().toString(), username, normalizedEmail, passwordhash, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getAvatarUrl() {return avatarUrl;}

    public void setAvatarUrl(String avatarUrl) {this.avatarUrl = avatarUrl;}
}
