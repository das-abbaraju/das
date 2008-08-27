package com.picsauditing.actions.users;

import java.util.Date;

import com.picsauditing.PICS.Utilities;
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
		super.execute();
		
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
			if (!isOK())
				return SUCCESS;
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
			user = null;
		}
		
		return SUCCESS;
	}
	
	private boolean isOK() throws Exception {
		if (user.getName().length() == 0)
			addActionError("Please enter a name");
		else if (user.getName().length() < 3)
			addActionError("Please enter a name with more than 2 characters");

		if (user.isGroup())
			return (getActionErrors().size() >= 0);
		
		if (user.getUsername().length() < 5)
			addActionError("Please choose a username at least 5 characters long");
			
		if (user.getPassword().length() < MIN_PASSWORD_LENGTH)
			addActionError("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		
		if (user.getPassword().equalsIgnoreCase(user.getUsername()))
			addActionError("Please choose a password different from your username.");

		if (user.getEmail().length() == 0 || !Utilities.isValidEmail(user.getEmail()))
			addActionError("Please enter a valid email address.");
		
		boolean ae = (getActionErrors().size() > 0);

		return (getActionErrors().size() >= 0);
	}
	
}
