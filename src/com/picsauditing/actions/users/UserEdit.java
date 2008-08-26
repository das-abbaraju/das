package com.picsauditing.actions.users;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;

public class UserEdit extends PicsActionSupport {
	protected User user;
	private UserDAO userDAO;
	
	private static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a password

	public UserEdit(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	public String execute() throws Exception {
		loadPermissions();
		permissions.tryPermission(OpPerms.EditUsers);
		if ("Save".equals(button)) {
			if (!permissions.hasPermission(OpPerms.AllOperators)) {
				user.setAccount(new Account());
				user.getAccount().setId(permissions.getAccountId());
			}
		}
		return SUCCESS;
	}
	
	private void deleteUser() throws Exception {
		permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
		addActionMessage("Successfully removed user: " + user.getUsername());
		userDAO.remove(user);
	}

	private boolean isOK() throws Exception {
//		if (userDO.name.length()==0)
//			errorMessages.addElement("Please enter a name");
//		else if (userDO.name.length() < 3)
//			errorMessages.addElement("Please enter a name with more than 2 characters");
//
//		if (userDO.isGroup.equals("Yes")) return (errorMessages.size() == 0);
//		
//		if (userDO.username.length() < 5)
//			errorMessages.addElement("Please choose a username at least 5 characters long");
//		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
//			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
//		if (userDO.password.equalsIgnoreCase(userDO.username))
//			errorMessages.addElement("Please choose a password different from your username.");
//		
//		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
//			errorMessages.addElement("Please enter a valid email address.");

		return (this.getActionErrors().size() > 0);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
