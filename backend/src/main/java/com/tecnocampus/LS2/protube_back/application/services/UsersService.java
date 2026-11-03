package com.tecnocampus.LS2.protube_back.application.services;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.adapter.in.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.application.port.in.UsersManagmentUseCase;
import com.tecnocampus.LS2.protube_back.application.port.out.UsersPort;
import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UsersManagmentUseCase {
    private final UsersPort usersPort;

    public UsersService(UsersPort usersPort) {
        this.usersPort = usersPort;
    }

    @Override
    public UserResponse createUser(RegisterRequest r) {
        Users user = Users.createUser(r.username(), r.email(), r.passwordhash());
        usersPort.saveUser(user);
        return new UserResponse(user.getUsername(), user.getEmail());
    }
}
