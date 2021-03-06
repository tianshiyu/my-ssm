package org.dishi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dishi.message.MailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

//@Component
public class MessagingService {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JmsTemplate jmsTemplate;

	public void sendMailMessage(MailMessage msg, String queue) throws Exception {
		String text = objectMapper.writeValueAsString(msg);
		jmsTemplate.send(queue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(text);
			}
		});
	}
}
