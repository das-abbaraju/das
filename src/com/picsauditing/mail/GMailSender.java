package com.picsauditing.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.picsauditing.jpa.entities.EmailQueue;

public class GMailSender extends javax.mail.Authenticator {
	private String user;
	private String password;
	private Session session;

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
}