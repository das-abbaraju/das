package com.picsauditing.mail;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

		message.setSentDate(email.getCreationDate());
		message.setSender(email.getFromAddress2());

		InternetAddress[] replyTo = {(InternetAddress)email.getFromAddress2()};
		message.setReplyTo(replyTo);
		
		message.setRecipients(RecipientType.TO, email.getToAddresses2());
		//message.setRecipients(RecipientType.CC, email.getCcAddresses2());
		//message.setRecipients(RecipientType.BCC, email.getBccAddresses2());

		message.setSubject(email.getSubject());
		message.setContent(email.getBody()+ (email.isHtml() ? "<br><br>" : "\n\n") + "sendmail", email.isHtml() ? "text/html" : "text/plain");
		Transport.send(message);
		// message.writeTo(System.out);
	}

}
