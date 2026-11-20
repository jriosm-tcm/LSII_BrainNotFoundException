package com.tecnocampus.LS2.protube_back.adapter.out;

import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsersRepository implements UsersPort {
    private final JdbcClient jdbcClient;

    public UsersRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void saveUser(Users user) {
        jdbcClient.sql("""
            INSERT INTO users (
                id,
                username,
                email,
                password_hash,
                created_at
            )
            VALUES (
                :id,
                :username,
                :email,
                :passwordHash,
                :createdAt
            )
            """)
                .param("id", user.getId())
                .param("username", user.getUsername())
                .param("email", user.getEmail())
                .param("passwordHash", user.getPasswordhash())
                .param("createdAt", user.getCreatedAt())
                .update();
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return jdbcClient.sql("""
            SELECT id, username, email, password_hash,
                   created_at, description, avatar_url
            FROM users
            WHERE email = :email
            """)
                .param("email", email)
                .query((rs, rowNum) -> {
                    Users user = new Users(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    user.setDescription(rs.getString("description"));
                    user.setAvatarUrl(rs.getString("avatar_url"));

                    return user;
                })
                .optional();
    }

    @Override
    public Optional<Users> findById(String id) {
        return jdbcClient.sql("""
        SELECT id, username, email, password_hash,
               created_at, description, avatar_url
        FROM users
        WHERE id = :id
        """)
                .param("id", id)
                .query((rs, rowNum) -> {
                    Users user = new Users(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    user.setDescription(rs.getString("description"));
                    user.setAvatarUrl(rs.getString("avatar_url"));

                    return user;
                })
                .optional();
    }

    @Override
    public boolean updateDescription(String id, String description) {
        return jdbcClient.sql("""
            UPDATE users
            SET description = :description
            WHERE id = :id
            """)
                .param("description", description)
                .param("id", id)
                .update() == 1;
    }

    @Override
    public boolean updateAvatarUrl(String id, String avatarUrl) {
        return jdbcClient.sql("""
            UPDATE users
            SET avatar_url = :avatarUrl
            WHERE id = :id
            """)
                .param("avatarUrl", avatarUrl)
                .param("id", id)
                .update() == 1;
    }


}
