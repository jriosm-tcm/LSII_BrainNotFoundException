package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import com.tecnocampus.LS2.protube_back.adapter.in.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.adapter.in.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.application.port.in.UsersManagmentUseCase;
import com.tecnocampus.LS2.protube_back.application.port.in.VideoManagementUseCase;
import org.h2.engine.User;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody RegisterRequest request) {
        UserResponse Response= userUseCase.createUser(request);
        return ResponseEntity.ok(Response);
    }
}
