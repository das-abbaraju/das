package com.picsauditing.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	public static void main(String arg[]) throws Exception {
		Properties p = System.getProperties();
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.host", "localhost");
		Session session = Session.getInstance(p);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress("info@picsauditing.com"));

		message.addRecipient(Message.RecipientType.TO, new InternetAddress("tallred@picsauditing.com"));
		message.setSubject("SendMail Test");
		String txt = "This is a test";
		message.setContent(txt, "text/plain");
		Transport.send(message);
		message.writeTo(System.out);
		System.out.println("Message Sent");
	}
}
