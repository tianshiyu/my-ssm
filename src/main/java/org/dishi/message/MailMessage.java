package org.dishi.message;

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

	public static MailMessage forgetPWD(String email, String captcha){
		MailMessage msg = new MailMessage();
		msg.email = email;
		msg.type = Type.FORGETPWD;
		msg.timestamp = System.currentTimeMillis();
		msg.data = new HashMap<>();
		msg.data.put("captcha", captcha);
		return msg;
	}
}
