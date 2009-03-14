package com.picsauditing.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.picsauditing.jpa.entities.EmailQueue;

public class SendMail {

	private Session session;
	
	public SendMail() {
		Properties p = System.getProperties();
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.host", "localhost");
		session = Session.getInstance(p);
	}

	public void send(EmailQueue email) throws Exception {
		MimeMessage message = new MimeMessage(session);
		
		DataHandler handler = new DataHandler(new ByteArrayDataSource(email.getBody()
				.getBytes(), email.isHtml() ? "text/html" : "text/plain"));

		message.setSentDate(email.getCreationDate());
		message.setSender(new InternetAddress(email.getFromAddress()));
		
		InternetAddress[] replyTo = {new InternetAddress(email.getFromAddress())};
		message.setReplyTo(replyTo);

		message.setRecipient(Message.RecipientType.TO, email.getToAddresses2()[0]);
		message.setRecipients(Message.RecipientType.CC, email.getToAddresses2());
		//message.setRecipients(RecipientType.TO, email.getToAddresses2());
		//message.setRecipients(RecipientType.CC, email.getCcAddresses2());
		//message.setRecipients(RecipientType.BCC, email.getBccAddresses2());

		message.setSubject(email.getSubject());
		message.setDataHandler(handler);
		Transport.send(message);
		// message.writeTo(System.out);
	}

}
