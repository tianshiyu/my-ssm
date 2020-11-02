package org.dishi.service.impl;

import org.dishi.dao.UserMapper;
import org.dishi.entity.User;
import org.dishi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private UserMapper userMapper;

    @Override
    public int register(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }

    @Override
    public User select(Integer id) {

        return null;
    }

    @Override
    public int update(User record) {
        return 0;
    }

    @Override
    public boolean validateEmailExist(String userEmail) {
        logger.info("邮箱："+userEmail);
        User user = userMapper.validateEmail(userEmail);
        return user!=null;
    }

    @Override
    public User login(String email, String password) {
        User user = userMapper.validateEmail(email);
        if(user!=null){
            if(user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }
}
