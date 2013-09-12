package com.picsauditing.mail;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;

@SuppressWarnings("serial")
public class EmailOptOut  extends PicsActionSupport {
	private String email;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	
	@Anonymous
	public String execute() throws Exception {			
		emailQueueDAO.addEmailAddressExclusions(email);
		return REDIRECT;
	}	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
