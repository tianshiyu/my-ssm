package org.dishi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloController {
    @GetMapping(value = {"/","hello.do","/hello.do"})
    public ModelAndView hello(){
        System.out.println("11111111111");
        return new ModelAndView("hello");
    }
}
