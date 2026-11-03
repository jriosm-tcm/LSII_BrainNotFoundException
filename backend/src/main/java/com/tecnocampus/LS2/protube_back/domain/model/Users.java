package com.tecnocampus.LS2.protube_back.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Users {
    private String id;
    private String username;
    private String email;
    private String passwordhash;
    private LocalDateTime createdAt;


    public Users(String id, String user, String email, String passwordhash, LocalDateTime createdAt) {
        this.id = id;
        this.username = user;
        this.email = email;
        this.passwordhash = passwordhash;
        this.createdAt = createdAt;
    }

    public static Users createUser(String username, String email, String passwordhash) {
        return new Users(UUID.randomUUID().toString(), username, email, passwordhash, LocalDateTime.now());
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
}
