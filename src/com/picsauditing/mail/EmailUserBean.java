package com.picsauditing.mail;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

public class EmailUserBean extends EmailBean {
	public EmailUserBean(UserDAO userDAO, AppPropertyDAO appPropertyDAO) {
		super(userDAO, appPropertyDAO);
	}
	
	public void sendMessage(EmailTemplates templateType, User user) throws Exception {
		this.templateType = templateType;
		
		tokens.put("user", user);
		email.setToAddress(user.getEmail());
		
		this.sendMail();
	}
	
	public void sendMessage(EmailTemplates templateType, int userID) throws Exception {
		User user = this.userDAO.find(userID);
		sendMessage(templateType, user);
	}
}
