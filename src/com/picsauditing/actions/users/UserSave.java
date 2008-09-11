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
		if (!forceLogin())
			return LOGIN;
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
			if (!isOK()){
				userDAO.clear();
				return SUCCESS;
			}
			if (user.getDateCreated() == null)
				user.setDateCreated(new Date());
			
			if (user.getAccount() == null) {
				user.setAccount(new Account());
				if (!permissions.hasPermission(OpPerms.AllOperators)) {
					user.getAccount().setId(permissions.getAccountId());
				} else
					user.getAccount().setId(accountId);
			}
			
			if (user.isGroup()) {
				// Create a unique username for this group
				String username = "GROUP";
				if (user.getAccount().getId() > 0)
					username += user.getAccount().getId();
				else {
					username += Math.round(Math.random()*10000);
					username += user.getName();
				}
				
				user.setUsername(username);
			}
			user = userDAO.save(user);
		}
		
		if ("Remove".equals(button)) {
			permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
			userDAO.remove(user);
			
			addActionMessage("Successfully removed " + 
					(user.isGroup() ? "group: " + user.getName() : "user: " + user.getUsername()));
			user = null;
		}
		
		return SUCCESS;
	}
	
	private boolean isOK() throws Exception {
		if (user == null) {
			addActionError("No user found");
			return false;
		}
		if (user.getName() == null || user.getName().length() == 0)
			addActionError("Please enter a Display Name.");
		else if (user.getName().length() < 3)
			addActionError("Please enter a Display Name with more than 2 characters.");

		if (user.isGroup())
			return (getActionErrors().size() == 0);
		
		if (user.getUsername() == null || user.getUsername().length() < 5)
			addActionError("Please choose a Username at least 5 characters long.");
		
		if (user.getUsername() != null && user.getUsername().length() >= 5) {
			if (userDAO.duplicateUsername(user.getUsername(), user.getId()))
				addActionError("That Username is already in use.  Please select another.");
		}
		
		if (user.getPassword() == null || user.getPassword().length() < MIN_PASSWORD_LENGTH)
			addActionError("Please choose a Password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		
		if (user.getPassword() != null && user.getPassword().equalsIgnoreCase(user.getUsername()))
			addActionError("Please choose a Password different from your username.");

		if (user.getEmail() == null || user.getEmail().length() == 0 || !Utilities.isValidEmail(user.getEmail()))
			addActionError("Please enter a valid Email address.");
		
		return getActionErrors().size() == 0;
	}
}
