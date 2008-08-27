package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.search.Report;
import com.picsauditing.util.SpringUtils;

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

		// Final security check
		if (user != null && user.getAccount() != null && user.getAccount().getId() != accountId) {
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
		if (userList == null)
			userList = userDAO.findByAccountID(accountId, isActive, isGroup);
		return userList;
	}

	public List<OpPerms> getGrantablePermissions() {
		List<OpPerms> list = new ArrayList<OpPerms>();
		for (UserAccess perm : permissions.getPermissions()) {
			// I can grant these permissions
			if (perm.getGrantFlag() == true)
				list.add(perm.getOpPerm());
		}
		for (UserAccess perm : user.getOwnedPermissions()) {
			// but these permissions, have already been granted
			list.remove(perm.getOpPerm());
		}
		return list;
	}

	public List<User> getAddableGroups() {
		List<User> list = new ArrayList<User>();

		List<User> activeGroups = userDAO.findByAccountID(accountId, "Yes", "Yes");
		for (User group : activeGroups) {
			// Add the groups I have access to
			if (permissions.hasPermission(OpPerms.AllOperators) || permissions.getGroups().contains(group.getId()))
				list.add(group);
		}
		for (UserGroup userGroup : user.getGroups()) {
			// but these groups, have already been added
			list.remove(userGroup.getGroup());
		}
		list.remove(user);
		return list;
	}

	public List<User> getAddableMembers() {
		List<User> list = new ArrayList<User>();
		if (permissions.hasPermission(OpPerms.AllOperators) || permissions.getGroups().contains(user.getId())) {
			// I'm an admin or I'm a member of this group

			list = userDAO.findByAccountID(accountId, "Yes", "");

			for (UserGroup userGroup : user.getMembers()) {
				// but users, already in the group
				list.remove(userGroup.getUser());
			}
		}
		list.remove(user);
		return list;
	}
	
	public List<UserLoginLog> getRecentLogins() {
		UserLoginLogDAO loginLogDao = (UserLoginLogDAO)SpringUtils.getBean("UserLoginLogDAO");
		return loginLogDao.findRecentLogins(user.getUsername(), 10);
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("user.id");
		user = userDAO.find(id);
	}

	public List<OperatorAccount> getFacilities() {
		facilities = operatorDao.findWhere(true, "");
		
		return facilities;
	}

	public Comparator<UserGroup> getGroupNameComparator() {
		return new Comparator<UserGroup>() {
			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getGroup().getName().compareTo(o2.getGroup().getName());
			}
		};
	}

	public Comparator<UserGroup> getUserNameComparator() {
		return new Comparator<UserGroup>() {
			@Override
			public int compare(UserGroup o1, UserGroup o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getUser().getName().compareTo(o2.getUser().getName());
			}
		};
	}

}
