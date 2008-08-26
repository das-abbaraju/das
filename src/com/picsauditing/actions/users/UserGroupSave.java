package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;

public class UserGroupSave extends UsersManage {
	protected int memberId;
	protected int groupId;
	protected int userGroupId;
	protected UserGroupDAO userGroupDAO;
	protected UserGroup userGroup = null;

	public UserGroupSave(OperatorAccountDAO operatorDao, UserDAO userDAO, UserGroupDAO userGroupDAO) {
		super(operatorDao, userDAO);
		this.userGroupDAO = userGroupDAO;
	}

	public String execute() {
		try {
			super.execute();
		} catch (Exception e) {
		}

		if ("AddGroup".equals(button)) {
			for (UserGroup userGroup : user.getMembers()) {
				if (userGroup.getGroup() != userGroup.getGroup()) {
					userGroupDAO.save(userGroup);
				}
			}
		}
		if ("RemoveGroup".equals(button)) {
			userGroupDAO.remove(userGroup.getUserGroupID());
		}
		if ("AddUser".equals(button)) {
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getUser() != userGroup.getUser()) {
					userGroupDAO.save(userGroup);
				}
			}
		}
		if ("RemoveGroup".equals(button)) {
			userGroupDAO.remove(userGroup.getUserGroupID());
		}

		return SUCCESS;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

}
