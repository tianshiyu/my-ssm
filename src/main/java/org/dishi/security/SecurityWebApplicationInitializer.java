package org.dishi.security;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


//注册security配置
public class SecurityWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //2.1创建AnnotationConfigWebApplicationContext
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        //2.2spring的配置类  springSecurity的配置类
        springContext.register(WebSecurityConfig.class);
    }
}
