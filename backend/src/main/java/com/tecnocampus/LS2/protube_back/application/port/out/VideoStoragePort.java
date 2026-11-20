package com.tecnocampus.LS2.protube_back.application.port.out;

import java.io.InputStream;

public interface VideoStoragePort {

    String save(InputStream content);
    void delete(String videoUrl);
}
