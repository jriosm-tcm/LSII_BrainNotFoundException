package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.adapter.in.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.application.port.in.UsersManagmentUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.PasswordHasher;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UsersManagmentUseCase {
    private final UsersPort usersPort;
    private final PasswordHasher passwordHasher;


    public UsersService(UsersPort usersPort, PasswordHasher passwordHasher) {
        this.usersPort = usersPort;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public UserResponse createUser(RegisterRequest r) {
        String passwordhash = passwordHasher.hash(r.password());
        Users user = Users.createUser(r.username(), r.email(), passwordhash);
        usersPort.saveUser(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
