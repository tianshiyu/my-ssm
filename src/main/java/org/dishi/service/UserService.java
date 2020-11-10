package org.dishi.service;

import org.dishi.entity.Role;
import org.dishi.entity.User;

import java.util.List;

public interface UserService {
    User register(User user);

    int delete(Integer id);

    User select(String email);

    User select(Integer id);

    int update(User record);

    boolean validateEmailExist(String userEmail);

    User login(String email, String password);

    List<Role> queryRoles(Integer uid);
}
