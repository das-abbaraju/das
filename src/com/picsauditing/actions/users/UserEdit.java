package com.picsauditing.actions.users;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.User;
import com.picsauditing.actions.PicsActionSupport;

public class UserEdit extends PicsActionSupport {
	protected String userId;
	protected String accountId;
	protected String action;

	public String execute() throws Exception {
		getPermissions();
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
