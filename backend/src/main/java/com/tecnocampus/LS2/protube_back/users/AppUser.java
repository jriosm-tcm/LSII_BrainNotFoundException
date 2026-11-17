package com.tecnocampus.LS2.protube_back.users;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false, length = 500)
    private String description = "";
    @Column(nullable = false, length = 500)
    private String avatarUrl = "";
    protected AppUser() {}
    public AppUser(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public String getDescription() { return description; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
