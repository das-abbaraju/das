package com.picsauditing.actions.users;

import java.util.Date;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.mail.EmailUserBean;

public class UserSave extends UsersManage {
	EmailUserBean mailer;
	private static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a password
	
	public UserSave(OperatorAccountDAO operatorDao, UserDAO userDAO, EmailUserBean mailer) {
		super(operatorDao, userDAO);
		this.mailer = mailer;
	}
	
	public String execute() throws Exception {
		try {
			super.execute();
		} catch (Exception e) {
			return SUCCESS;
		}
		
		if ("sendWelcomeEmail".equals(button) && user != null) {
			try {
				mailer.sendMessage(EmailTemplates.welcome, user);
			} catch (Exception e) {
				addActionError(e.getMessage());
				return SUCCESS;
			}
			addActionMessage("Welcome Email sent to " + user.getEmail());
		}
		
		if ("Save".equals(button)) {
			if (user.getAccount() == null) {
				addActionMessage("Users must relogin for changes to take effect");
				user.setAccount(new Account());
				if (!permissions.hasPermission(OpPerms.AllOperators)) {
					if (permissions.hasPermission(OpPerms.AllOperators))
						user.getAccount().setId(accountId);
				} else
					user.getAccount().setId(permissions.getAccountId());

				if (user.getDateCreated() == null)
					user.setDateCreated(new Date());
				user = userDAO.save(user);
			}
		}
		
		if ("Remove".equals(button)) {
			permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
			userDAO.remove(user);

			addActionMessage("Successfully removed user: " + user.getUsername());
		}
		
		return SUCCESS;
	}
	
//	private void deleteUser() throws Exception {
//		permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
//		addActionMessage("Successfully removed user: " + user.getUsername());
//		userDAO.remove(user);
//	}

	private boolean isOK() throws Exception {
//		if (userDO.name.length()==0)
//			addActionMessage.addElement("Please enter a name");
//		else if (userDO.name.length() < 3)
//			addActionMessage.addElement("Please enter a name with more than 2 characters");
//
//		if (userDO.isGroup.equals("Yes")) return (errorMessages.size() == 0);
//		
//		if (userDO.username.length() < 5)
//			addActionMessage.addElement("Please choose a username at least 5 characters long");
//		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
//			addActionMessage.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
//		if (userDO.password.equalsIgnoreCase(userDO.username))
//			addActionMessage.addElement("Please choose a password different from your username.");
//		
//		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
//			addActionMessage.addElement("Please enter a valid email address.");

		return (this.getActionErrors().size() > 0);
	}
	
}
