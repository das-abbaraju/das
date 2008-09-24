package com.picsauditing.mail;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class EmailUserBean extends EmailBean {
	public EmailUserBean(UserDAO userDAO, AppPropertyDAO appPropertyDAO, EmailQueueDAO emailQueueDAO) {
		super(userDAO, appPropertyDAO, emailQueueDAO);
	}
	
	public void sendMessage(EmailTemplates templateType, User user) throws Exception {
		this.templateType = templateType;
		
		tokens.put("user", user);
		email.setToAddresses(user.getEmail());
		this.sendMail();
	}
	
	public void sendMessage(EmailTemplates templateType, int userID) throws Exception {
		User user = this.userDAO.find(userID);
		sendMessage(templateType, user);
	}

	public void sendMessage(EmailTemplates templateType, ContractorAudit conAudit) throws Exception {
		this.templateType = templateType;
		tokens.put("confirmLink", getServerName()+"ScheduleAuditUpdate.action?type=a&auditID="+conAudit.getId()+"&key="+Strings.hashUrlSafe("a"+conAudit.getAuditor().getId()+"id"+conAudit.getId()));
		tokens.put("conAudit", conAudit);
		email.setToAddresses(conAudit.getAuditor().getEmail());
		this.sendMail();
	}



}
