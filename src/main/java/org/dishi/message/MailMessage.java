package org.dishi.message;

import org.dishi.entity.User;

import java.util.HashMap;
import java.util.Map;

public class MailMessage {

	public enum Type {
		REGISTRATION, LOGIN, FORGETPWD;
	}

	public Type type;
	public String email;
	public String name;
	public long timestamp;
	public Map<String, String> data;

	private MailMessage(){}

	public static MailMessage registration(String email, String name) {
		MailMessage msg = new MailMessage();
		msg.email = email;
		msg.name = name;
		msg.type = Type.REGISTRATION;
		msg.timestamp = System.currentTimeMillis();
		return msg;
	}

	public static MailMessage forgetPWD(User user, String url){
		MailMessage msg = new MailMessage();
		msg.email = user.getEmail();
		msg.type = Type.FORGETPWD;
		msg.timestamp = System.currentTimeMillis();
		msg.data = new HashMap<>();
		msg.data.put("name", user.getUsername());
		msg.data.put("url", url);
		return msg;
	}
}
