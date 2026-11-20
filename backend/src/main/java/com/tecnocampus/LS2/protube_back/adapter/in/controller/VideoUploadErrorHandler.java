package com.tecnocampus.LS2.protube_back.adapter.in.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestControllerAdvice(
        assignableTypes = VideoUploadController.class
)
public class VideoUploadErrorHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(VideoUploadErrorHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleInvalidUpload(
            IllegalArgumentException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            IOException.class,
            UncheckedIOException.class
    })
    public ProblemDetail handleStorageError(Exception exception) {
        LOG.error("No se ha podido guardar el vídeo.", exception);

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No se ha podido guardar el vídeo."
        );
    }
}
