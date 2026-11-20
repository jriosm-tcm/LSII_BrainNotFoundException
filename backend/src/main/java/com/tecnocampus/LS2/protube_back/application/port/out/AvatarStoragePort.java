package com.tecnocampus.LS2.protube_back.application.port.out;

public interface AvatarStoragePort {

    String save(byte[] content);

    void delete(String avatarUrl);
}
