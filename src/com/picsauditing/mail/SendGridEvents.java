package com.picsauditing.mail;

import java.util.Date;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class SendGridEvents extends PicsActionSupport {
	private EmailQueueDAO queueDAO;
	
	private String event;
	private String email;
	private String url;
	private String status;
	private String reason;
	
	public SendGridEvents(EmailQueueDAO queueDAO) {
		this.queueDAO = queueDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (!Strings.isEmpty(event) && !Strings.isEmpty(email)) {
			EmailQueue e = new EmailQueue();
			e.setFromAddress("PICS Mailer <info@picauditing.com>");
			e.setToAddresses("Lani Aung <laung@picsauditing.com>");
			e.setSubject("SendGrid Event - " + event);
			
			String body = null;
			if (event.equals("click") && !Strings.isEmpty(url))
				body = email + " clicked on the link " + url;
			else if (event.equals("open"))
				body = email + " opened email";
			else if (event.equals("unsubscribe"))
				body = email + " unsubscribed";
			else if (event.equals("bounce") && !Strings.isEmpty(status) && !Strings.isEmpty(reason))
				body = email + " bounced with code " + status + " because " + reason;
			else if (event.equals("spamreport"))
				body = email + " marked email as spam";
			else {
				body = "Event type '" + event + "' from <" + email + "> was not recognized.\n"
					+ "\nURL: " + (Strings.isEmpty(url) ? "" : url)
					+ "\nStatus: " + (Strings.isEmpty(status) ? "" : status)
					+ "\nReason: " + (Strings.isEmpty(reason) ? "" : reason);
			}
			
			body += "\n\n" + new Date();
			
			e.setBody(body);
			e.setCreationDate(new Date());
			
			queueDAO.save(e);
			
			EmailSender sender = new EmailSender();
			sender.sendNow(e);
		}
		
		return SUCCESS;
	}
	
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
}