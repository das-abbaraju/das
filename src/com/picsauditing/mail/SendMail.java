package com.picsauditing.mail;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;

public class SendMail {

	private Session session;
	private EmailAttachmentDAO attachmentDAO;
	
	public SendMail() {
		Properties p = System.getProperties();
		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.host", "localhost");
		session = Session.getInstance(p);
	}

	public void send(EmailQueue email) throws Exception {
		MimeMessage message = new MimeMessage(session);

		DataHandler handler = new DataHandler(new ByteArrayDataSource(email.getBody().getBytes(),
				email.isHtml() ? "text/html" : "text/plain"));

		message.setSentDate(email.getCreationDate());
		message.setFrom(email.getFromAddress2());

		message.setRecipients(RecipientType.TO, email.getToAddresses2());
		message.setRecipients(RecipientType.CC, email.getCcAddresses2());
		message.setRecipients(RecipientType.BCC, email.getBccAddresses2());

		message.setSubject(email.getSubject());
		message.setDataHandler(handler);
		
		List<EmailAttachment> attachments = attachmentDAO.findByEmailID(email.getId());

		if (attachments != null && attachments.size() > 0) {
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			
			// Add in the text in the email
			mbp.setText(email.getBody());
			mp.addBodyPart(mbp);
			
			for (EmailAttachment attachment : attachments) {
				mbp = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(attachment.getFileName());
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(fds.getName());
				mp.addBodyPart(mbp);
			}
			
			message.setContent(mp);
		}
		
		Transport.send(message);
	}
	
	public void setAttachmentDAO(EmailAttachmentDAO attachmentDAO) {
		this.attachmentDAO = attachmentDAO;
	}
}
