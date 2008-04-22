package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

public class PicsActionSupport extends ActionSupport {
	protected Permissions permissions = null;
	protected boolean autoLogin = false;
	protected String message;
	
	private User user; // Current logged in user
	private Account account; // Current logged in user's account

	protected boolean getPermissions() throws Exception {
		permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		if (permissions == null) {
			permissions = new Permissions();
			if (this.autoLogin) {
				// Auto Login tallred
				com.picsauditing.access.User user = new com.picsauditing.access.User();
				user.setFromDB("941"); // tallred
				permissions.login(user);
				ActionContext.getContext().getSession().put("permissions", permissions);
			}
		}

		if (!permissions.loginRequired(ServletActionContext.getResponse(), ServletActionContext.getRequest())) {
			return false;
		}
		return true;
	}
	
	protected void tryPermissions(OpPerms opPerms) throws Exception {
		permissions.tryPermission(opPerms, OpType.View);
	}

	protected void tryPermissions(OpPerms opPerms, OpType opType) throws Exception {
		getPermissions();
		permissions.tryPermission(opPerms, opType);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public User getUser() {
		if (user == null) {
			UserDAO dao = (UserDAO)SpringUtils.getBean("UserDAO");
			user = dao.find(permissions.getUserId());
		}
		return user;
	}
	
	public Account getAccount() {
		if (account == null) {
			AccountDAO dao = (AccountDAO)SpringUtils.getBean("AccountDAO");
			account = dao.find(permissions.getAccountId(), permissions.getAccountType());
		}
		return account;
	}
	
	public OperatorAccount getOperatorAccount() {
		Account operator = getAccount();
		if (operator.getType().equals("Operator"))
			return (OperatorAccount)operator;
		return null;
	}
}
