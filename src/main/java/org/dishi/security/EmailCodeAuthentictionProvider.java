package org.dishi.security;

import org.dishi.entity.Role;
import org.dishi.entity.User;
import org.dishi.entity.UserRole;
import org.dishi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class EmailCodeAuthentictionProvider implements AuthenticationProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    UserService userService;
    PasswordEncoder passwordEncoder;

    public EmailCodeAuthentictionProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 认证
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.info("EmailCodeAuthentictionProvider运行");

        EmailCodeAuthenticationToken token = (EmailCodeAuthenticationToken)authentication;

        User user = userService.select((String)token.getPrincipal());

        if(user == null){
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        if(!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())){
            logger.info("密码比对1："+passwordEncoder.matches(user.getPassword(), authentication.getCredentials().toString()));
            logger.info("密码比对1："+passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword()));
            throw new BadCredentialsException("Bad credentials");
        }

        List<Role> roles = userService.queryRoles(user.getId());
        UserDetails userDetails = new UserRole(roles, user);

        logger.info("username:"+userDetails.getUsername());

        UsernamePasswordAuthenticationToken result =
                new UsernamePasswordAuthenticationToken(userDetails,user.getPassword(),userDetails.getAuthorities());
                /*
                Details 中包含了 ip地址、 sessionId 等等属性
                */
        result.setDetails(token.getDetails());

        logger.info("EmailCodeAuthentictionProvider运行结束");
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
