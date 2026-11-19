package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.application.port.in.RegisterUserCommand;
import com.tecnocampus.LS2.protube_back.adapter.in.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.application.port.in.UsersManagmentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsersController {

        private final UsersManagmentUseCase userUseCase;

        public UsersController  (UsersManagmentUseCase userUseCase) {
            this.userUseCase = userUseCase;
        }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid RegisterUserCommand request) {
        return ResponseEntity.ok(userUseCase.createUser(request));
    }

    //login
    //logout
}
