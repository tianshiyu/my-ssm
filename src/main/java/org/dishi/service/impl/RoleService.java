package org.dishi.service.impl;

import org.dishi.dao.RoleMapper;
import org.dishi.dao.UserMapper;
import org.dishi.entity.Role;
import org.dishi.entity.User;
import org.dishi.entity.UserRole;
import org.dishi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements UserDetailsService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    UserService userService;

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.validateEmail(s);

        logger.info("标记email："+s);
        logger.info("标记："+user);

        if (user == null) {
            //避免返回null，这里返回一个不含有任何值的User对象，在后期的密码比对过程中一样会验证失败
            return new UserRole();
        }
        //查询用户的角色信息，并返回存入user中
        List<Role> roles = roleMapper.getRolesByUid(user.getId());

        return new UserRole(roles, user);
    }

    public UserDetails getUserById(Integer id){
        String email = userMapper.selectByPrimaryKey(id).getEmail();
        return loadUserByUsername(email);
    }
}
