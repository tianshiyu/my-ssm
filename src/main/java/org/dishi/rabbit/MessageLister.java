package org.dishi.rabbit;

import com.rabbitmq.client.Channel;
import org.dishi.message.MailMessage;
import org.dishi.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class MessageLister {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    EmailService emailService;
    @RabbitListener(queues = "email_queue")
    public void handler(Message message, Channel channel) throws IOException {
        MailMessage mailMessage = (MailMessage) message.getPayload();
        MessageHeaders headers = message.getHeaders();
        Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        String msgId = (String) headers.get("spring_returned_message_correlation");
        logger.info("tag:"+tag);
        logger.info("msgId:"+msgId);
        try {
            emailService.send(mailMessage);
            channel.basicAck(tag, false);
        }catch (RuntimeException e) {
            channel.basicNack(tag, false, true);
            e.printStackTrace();
            logger.error("邮件发送失败：" + e.getMessage());
        }
    }
}
