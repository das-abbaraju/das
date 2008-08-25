package com.picsauditing.actions.users;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.User;
import com.picsauditing.actions.PicsActionSupport;

public class UserEdit extends PicsActionSupport {
	protected String userId;
	protected String accountId;
	protected String action;
	private static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a password

	public String execute() throws Exception {
		loadPermissions();
		permissions.tryPermission(OpPerms.EditUsers);
		if (!permissions.hasPermission(OpPerms.AllOperators)) {
			accountId = permissions.getAccountIdString();
		}
		return SUCCESS;
	}
	
	private void deleteUser() throws Exception {
		permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
		User user = new User();
		user.setFromDB(userId);
		String name = user.userDO.name;
		user.deleteUser();
		//
		//%>Successfully deleted <%=name %><br />
		//<a href="users_manage.jsp">Refresh User List</a><%
		//return;
	}

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (userDO.name.length()==0)
			errorMessages.addElement("Please enter a name");
		else if (userDO.name.length() < 3)
			errorMessages.addElement("Please enter a name with more than 2 characters");

		if (userDO.isGroup.equals("Yes")) return (errorMessages.size() == 0);
		
		if (userDO.username.length() < 5)
			errorMessages.addElement("Please choose a username at least 5 characters long");
		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		if (userDO.password.equalsIgnoreCase(userDO.username))
			errorMessages.addElement("Please choose a password different from your username.");
		
		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
			errorMessages.addElement("Please enter a valid email address.");

		return (errorMessages.size() == 0);
	}//isOK

	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
