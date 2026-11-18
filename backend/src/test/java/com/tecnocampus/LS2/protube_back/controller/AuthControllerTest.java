package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.services.AuthService;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Test void duplicateEmailIsRejected() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);
        AuthController controller = new AuthController(new AuthService(repository, new BCryptPasswordEncoder()));
        var response = controller.register(new AuthController.Registration("TEST@example.com", "password123"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(repository, never()).saveAndFlush(any());
    }
}
