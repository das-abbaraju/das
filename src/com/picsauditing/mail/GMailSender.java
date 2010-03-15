package com.picsauditing.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;

public class GMailSender extends javax.mail.Authenticator {
	private String user;
	private String password;
	private Session session;
	private EmailAttachmentDAO attachmentDAO;

	public GMailSender(String user, String password) {
		this.user = user;
		this.password = password;

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		session = Session.getDefaultInstance(props, this);
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}

	public synchronized void sendMail(EmailQueue email) throws MessagingException {
		MimeMessage message = new MimeMessage(session);

		DataHandler handler = new DataHandler(new ByteArrayDataSource(email.getBody().getBytes(),
				email.isHtml() ? "text/html" : "text/plain"));

		message.setSentDate(email.getCreationDate());
		//message.setSender(new InternetAddress(user));
		message.setFrom(email.getFromAddress2());

		if (!email.getFromAddress2().getAddress().equals(user)) {
			InternetAddress[] replyTo = { new InternetAddress(email.getFromAddress()) };
			message.setReplyTo(replyTo);
		}

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
		
		// DataSource ds = new ByteArrayDataSource(email.getBody().getBytes(), email.isHtml() ? "text/html" :
		// "text/plain");
		// message.setDataHandler(new DataHandler(ds));
		Transport.send(message);
	}

	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		/**
		 * Create a DataSource from a String
		 * 
		 * @param data
		 *            is the contents of the mail message
		 * @param type
		 *            is the mime-type such as text/html
		 */
		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
	
	public void setAttachmentDAO(EmailAttachmentDAO attachmentDAO) {
		this.attachmentDAO = attachmentDAO;
	}
}