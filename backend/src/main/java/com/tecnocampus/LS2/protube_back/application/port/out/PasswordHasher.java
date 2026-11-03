package com.tecnocampus.LS2.protube_back.application.port.out;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String password, String storedHash);
}
