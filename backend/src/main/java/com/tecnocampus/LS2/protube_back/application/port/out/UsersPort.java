package com.tecnocampus.LS2.protube_back.application.port.out;

import com.tecnocampus.LS2.protube_back.domain.model.Users;
import org.apache.catalina.User;

public interface UsersPort {

    void saveUser(Users user);
}
