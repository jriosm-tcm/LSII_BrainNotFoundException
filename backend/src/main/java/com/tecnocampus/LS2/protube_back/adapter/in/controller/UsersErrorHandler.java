package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.UncheckedIOException;

import java.util.List;

@RestControllerAdvice(assignableTypes = {
        UsersController.class,
        ProfileController.class
})
public class UsersErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidRegistration(MethodArgumentNotValidException ex) {
        List<FieldValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String field = error.getField();
                    if (field.equals("passwordWithinBcryptLimit")) {
                        field = "password";
                    }
                    return new FieldValidationError(field, error.getDefaultMessage());
                })
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Revisa los campos enviados."
        );
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail handleDuplicateEmail() {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo."
        );
    }

    private record FieldValidationError(String field, String message) {
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleInvalidArgument(
            IllegalArgumentException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(UncheckedIOException.class)
    public ProblemDetail handleFileStorageError() {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se ha podido guardar la imagen."
        );
    }
}
