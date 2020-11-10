package org.dishi.controller.user.login;

import org.dishi.controller.BaseController;
import org.dishi.entity.User;
import org.dishi.message.MailMessage;
import org.dishi.utils.UserUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class LoginController extends BaseController {
    @GetMapping(value = {"/login.do"})
    public ModelAndView login(){
        return new ModelAndView("login.html");
    }

    @PostMapping(value = {"/login.do"})
    public ModelAndView doLogin(@RequestParam String email, @RequestParam String password,
                                @RequestParam String captcha, HttpSession session){

        User user = userService.login(email, password);
        if(user!=null){
            session.setAttribute("user", user);
            return new ModelAndView("index.html");
        }
        return new ModelAndView("login.html");
    }

    @GetMapping("/logout.do")
    public ModelAndView logout(HttpSession session){
        session.removeAttribute("user");
        return new ModelAndView("index.html");
    }

    @RequestMapping("/getUser.do")
    @ResponseBody
    public Map<String, Object> getUser() {

        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        User user = UserUtil.getUserFromSession();
        if (user != null) {
            resultMap.put("user", user);
        }
        return resultMap;
    }

    @GetMapping("/loginFailed.do")
    @ResponseBody
    public String loginFailed(){
        return "loginFailed";
    }
}
