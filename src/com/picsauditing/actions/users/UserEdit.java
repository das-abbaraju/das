package com.picsauditing.actions.users;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.User;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectUser;

public class UserEdit extends PicsActionSupport {
	protected String userId;
	protected String accountId;
	protected String action;

	public String execute() throws Exception {
		if (getPermissions(OpPerms.EditUsers, OpType.View)) {
			permissions.tryPermission(OpPerms.EditUsers, OpType.View);
			if (!permissions.hasPermission(OpPerms.AllOperators)) {
				accountId = permissions.getAccountIdString();
			}
			
			return SUCCESS;
		}
		return INPUT;
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
