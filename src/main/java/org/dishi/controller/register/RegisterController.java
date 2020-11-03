package org.dishi.controller.register;

import org.dishi.controller.BaseController;
import org.dishi.entity.User;
import org.dishi.message.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class RegisterController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(value = {"/register.do"})
    public ModelAndView register() {
        return new ModelAndView("register.html");
    }

    @PostMapping(value = {"/register.do"})
    public ModelAndView doRegister(@RequestParam("name") String name, @RequestParam("email") String email,
                                 @RequestParam("password") String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(name);
        user.setRegtime(new Date());

        if(userService.register(user)==1) {
            MailMessage mm = MailMessage.registration(email, name);
            emailService.send(mm);
            return new ModelAndView("countDown.html");
        }
        return new ModelAndView("index.html");
    }

    @RequestMapping("/validateEmail.do")
    public void validateEmail(String userEmail, PrintWriter writer){
        if (userService.validateEmailExist(userEmail)) {
            logger.info("邮箱已注册");
            writer.write("hasEmail");
        } else {
            logger.info("邮箱未注册");
            writer.write("noEmail");
        }
    }
}
