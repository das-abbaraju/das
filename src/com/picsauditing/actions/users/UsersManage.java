package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Report;

public class UsersManage extends PicsActionSupport implements Preparable {
	private static final long serialVersionUID = -167727120482502678L;
	
	protected int accountId = 0;
	protected User user;

	protected String filter = null;
	protected List<OperatorAccount> facilities = null;
	protected Report search = null;
	protected List<User> userList = null;

	protected boolean filtered = false;
	
	protected String isGroup = "";
	protected String isActive = "Yes";

	protected boolean hasAllOperators = false;

	protected OperatorAccountDAO operatorDao;
	protected UserDAO userDAO;

	public UsersManage(OperatorAccountDAO operatorDao, UserDAO userDAO) {
		this.operatorDao = operatorDao;
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EditUsers);

		// Make sure we can edit users in this account
		if (accountId == 0)
			accountId = permissions.getAccountId();
		if (permissions.getAccountId() != accountId)
			permissions.tryPermission(OpPerms.AllOperators);
		
		userList = userDAO.findByAccountID(accountId, isActive, isGroup);

		if (user != null && accountId != user.getAccount().getId()) {
			this.addActionError(user.getName() + " was not listed in this account");
			user = null;
		}
		
		return SUCCESS;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		if (isGroup != null && isGroup.length() > 0)
			filtered = true;
		
		this.isGroup = isGroup;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		if (!isActive.equals("Yes"))
			filtered = true;

		this.isActive = isActive;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<User> getUserList() {
		return userList;
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("user.id");
		user = userDAO.find(id);
	}

	public List<OperatorAccount> getFacilities() {
		facilities = new ArrayList<OperatorAccount>();
		return facilities;
	}
}
