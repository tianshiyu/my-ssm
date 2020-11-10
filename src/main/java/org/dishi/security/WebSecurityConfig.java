package org.dishi.security;

import org.dishi.service.UserService;
import org.dishi.service.impl.RoleService;
import org.dishi.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RoleService roleService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    EmailCodeAuthenticationSecurityConfig emailCodeAuthenticationSecurityConfig;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//        auth
//                //注入继承UserDetailsService的Service,能够与数据库进行交互
//                .userDetailsService(roleService)
//                //设置加密方式
//                .passwordEncoder(passwordEncoder);
//        auth
//                //增加provider
//                .authenticationProvider(new EmailCodeAuthentictionProvider(userService, bCryptPasswordEncoder()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                //不拦截的请求地址
                .ignoring()
                .antMatchers("/", "/static/**", "/register.do");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * authorizeRequests()  配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
         * formLogin()          对应表单认证相关的配置
         * logout()             对应了注销相关的配置
         * httpBasic()          可以配置basic登录
         * etc
         * sessionManagement()  session管理
         */
        http
                //配置路径拦截，表明路径访问所对应的权限，角色，认证信息。
                .authorizeRequests()
                // 表示/hello路径登录即可访问
                .antMatchers("/hello").authenticated()
//                .antMatchers("/hello").hasRole("超级管理员")
                .antMatchers("/profile").authenticated()
                // 表示/admin/**路径需有"admin"角色方可访问
                .antMatchers("/admin/**").hasRole("超级管理员")
                .antMatchers("/memo.do").hasRole("用户")
                //其余所有请求
//                .anyRequest().authenticated()

                //返回HttpSecurity对象，配置其他
                .and()
                //对应表单认证相关的配置
                .formLogin()
//                //登录成功的处理器
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//                        httpServletResponse.setContentType("application/json;charset=utf-8");
//                        PrintWriter out = httpServletResponse.getWriter();
//                        httpServletRequest.getSession().setAttribute("__user__", UserUtil.getUserFromSession());
//                        out.write("{\"status\":\"success\",\"msg\":\"登录成功\"}");
//                        out.flush();
//                        out.close();
//                    }
//                })
//                //登录失败的处理器
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
//                        httpServletResponse.setContentType("application/json;charset=utf-8");
//                        PrintWriter out = httpServletResponse.getWriter();
//                        out.write("{\"status\":\"error\",\"msg\":\"登录失败\"}");
//                        out.flush();
//                        out.close();
//                    }
//                })
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.do")
//                //登录处理页面
//                .loginProcessingUrl("/login.do")
                //默认登录成功后跳转的url
                .defaultSuccessUrl("/")
//                //定义登录时，用户名的 key，默认为 username
//                .usernameParameter("email")
//                //定义登录时，用户密码的 key，默认为 password
//                .passwordParameter("password")
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()

                //记住我功能
                .rememberMe()
//                .tokenRepository(persistentTokenRepository)
                //设置Token的有效时间
                .rememberMeServices(emailCodeAuthenticationSecurityConfig.getRememberMeServices())
                //使用userDetailsService用Token从数据库中获取用户自动登录
//                .userDetailsService(roleService)
                .and()


                //对应了注销相关的配置
                .logout()
                .logoutUrl("/logout.do")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()

                //禁用CSRF
                .csrf().disable()

                //禁用httpBasic
                .httpBasic().disable()

                //异常配置
                .exceptionHandling()
                .accessDeniedPage("/403")
//                //权限不足处理器
//                .accessDeniedHandler(getAccessDeniedHandler())
                .and()

                //session管理
                .sessionManagement()
                //设置每个用户的最大并发会话数量
                .maximumSessions(1);

        http.headers().frameOptions().sameOrigin();

        http.apply(emailCodeAuthenticationSecurityConfig);
    }

//    //权限不足处理器
//    @Bean
//    AccessDeniedHandler getAccessDeniedHandler() {
//        return new AuthenticationAccessDeniedHandler();
//    }


}
