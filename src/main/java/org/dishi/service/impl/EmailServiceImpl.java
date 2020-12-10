package org.dishi.service.impl;

import org.dishi.entity.Memo;
import org.dishi.message.MailMessage;
import org.dishi.service.EmailService;
import org.dishi.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender mailSender;
    @Autowired
    MemoService memoService;

    @Value("${smtp.from}")
    String from;

    @Override
    public void send(MailMessage mm){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(from);
            helper.setTo(mm.email);
            String html;
            switch (mm.type) {
                case REGISTRATION:
                    helper.setSubject("注册");
                    html = String.format("<p>Hi, %s,</p><p>感谢注册</p><p>Sent at %s</p>", mm.name, LocalDateTime.now());
                    break;
                case FORGETPWD:
                    helper.setSubject("修改密码");
                    html = String.format("<p>尊敬的%s,</p><p>链接为：%s</p><p>Sent at %s</p>", mm.data.get("name"),mm.data.get("url"), LocalDateTime.now());
                    break;
                case MEMO:
                    helper.setSubject("备忘录");
                    html = String.format("<p>尊敬的%s,</p><p>备忘录如下：%s</p><p>Sent at %s</p>", mm.data.get("name"),mm.data.get("content"), LocalDateTime.now());
                    Memo memo = memoService.select(Integer.valueOf(mm.data.get("mid")));
                    memo.setState(1);
                    memoService.updateMemo(memo);
                    break;
                case LOGIN:
                    helper.setSubject("登陆");
                    html = String.format("<p>Hi, %s,</p><p>欢迎登陆</p><p>Sent at %s</p>", mm.name, LocalDateTime.now());
                    break;
                default:
                    throw new RuntimeException("邮件类型错误");
            }
            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
