package org.dishi.controller;

import org.dishi.entity.User;
import org.dishi.utils.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class UtilController {
    @GetMapping(value = {"/index.do"})
    public ModelAndView index(){
        return new ModelAndView("index.html");
    }

    @GetMapping("/getUser.do")
    @ResponseBody
    public Map<String, Object> getUser() {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        User user = UserUtil.getUserFromSession();
        if (user != null) {
            resultMap.put("user", user);
        }
        return resultMap;
    }

    @RequestMapping("/countDown.do")
    public ModelAndView cd(){
        return new ModelAndView("countDown.html");
    }
}
