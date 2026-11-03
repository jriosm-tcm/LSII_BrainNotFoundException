package com.tecnocampus.LS2.protube_back.application.port.in;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.adapter.in.dto.UserResponse;

public interface UsersManagmentUseCase {
    UserResponse createUser(RegisterRequest r);
}



