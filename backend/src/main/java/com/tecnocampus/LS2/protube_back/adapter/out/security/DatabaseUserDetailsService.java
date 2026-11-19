package com.tecnocampus.LS2.protube_back.adapter.out.security;

import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UsersPort usersPort;

    public DatabaseUserDetailsService(UsersPort usersPort) {
        this.usersPort = usersPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        var user = usersPort.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Credenciales incorrectas."
                        )
                );

        return User.withUsername(user.getEmail())
                .password(user.getPasswordhash())
                .roles("USER")
                .build();
    }
}
