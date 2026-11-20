package com.tecnocampus.LS2.protube_back.application.exception;

public class VideoNotOwnedException extends RuntimeException {

    public VideoNotOwnedException() {
        super("Solo el propietario puede modificar este vídeo.");
    }
}
