package org.dishi.service;

import org.dishi.entity.User;

public interface UserService {
    int register(User user);

    int delete(Integer id);

    User select(Integer id);

    int update(User record);

    boolean validateEmailExist(String userEmail);

    User login(String email, String password);
}
