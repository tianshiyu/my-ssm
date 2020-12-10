package org.dishi.controller.user.forget;

import org.dishi.controller.BaseController;
import org.dishi.entity.User;
import org.dishi.message.MailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ForgetController extends BaseController {
    @GetMapping("/forget.do")
    public ModelAndView forget() {
        return new ModelAndView("forget.html");
    }

    @PostMapping("/forget.do")
    public ModelAndView doForget(@RequestParam String userEmail) {
        //判断是否有该用户
        User user = null;
        if (userEmail != null) {
            user = userService.select(userEmail);
        }
        //提示用户发送了邮件，让用户重新设置密码账户
        String url = "http://localhost:8080/reset.do?userId=" + user.getId();
        rabbitTemplate.convertAndSend("email_exchange", "email_key", MailMessage.forgetPWD(user, url));
//        emailService.send(MailMessage.forgetPWD(user, url));
        return new ModelAndView("countDown.html");
    }

    @GetMapping("/reset.do")
    public ModelAndView reset(@RequestParam String userId) {
        return new ModelAndView("resetView.html");
    }

    @PostMapping("/reset.do")
    @ResponseBody
    public Map<String, String> doReset(User user) {
        System.out.println("重置密码："+user);
        Map<String, String> resultMap = new LinkedHashMap<>();
        if (user != null && user.getId() != null) {
            User newUser = userService.select(user.getId());
            newUser.setPassword(user.getPassword());
            System.out.println("新密码："+user.getPassword());

            int num = userService.update(newUser);
            if (num > 0) {
                resultMap.put("message", "修改成功");
            } else {
                resultMap.put("message", "修改失败");
            }
            return resultMap;
        }
        return null;
    }
}
