package org.dishi.security;

import org.dishi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class EmailCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private MyAuthenticationFailHandler myAuthenticationFailHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private UserService userService ;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsService roleService;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    private MyRememberMeServices rememberMeServices;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init(){
        rememberMeServices = new MyRememberMeServices(UUID.randomUUID().toString(), roleService, persistentTokenRepository);
        rememberMeServices.setTokenValiditySeconds(60*60);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        logger.info("EmailCodeAuthenticationSecurityConfig运行");

        myAuthenticationFailHandler.setTar("email");
        EmailCodeAuthenticationFilter filter = new EmailCodeAuthenticationFilter(new AntPathRequestMatcher("/login.do","POST")) ;
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(myAuthenticationFailHandler);
        filter.setRememberMeServices(rememberMeServices);

        EmailCodeAuthentictionProvider provider = new EmailCodeAuthentictionProvider(userService, passwordEncoder) ;
        http
                .authenticationProvider(provider)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

    }

    public MyRememberMeServices getRememberMeServices() {
        return rememberMeServices;
    }
}
