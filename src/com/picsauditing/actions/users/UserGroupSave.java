package com.picsauditing.actions.users;

import java.util.Date;

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
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getGroup().getId() != groupId) {
					UserGroup uGroup = new UserGroup();
					uGroup.setGroup(new User());
					uGroup.getGroup().setId(groupId);
					uGroup.setUser(user);
					uGroup.setCreationDate(new Date());
					uGroup.setCreatedBy(permissions.getUserId());
					userGroupDAO.save(uGroup);
				}
			}
		}
		if ("RemoveGroup".equals(button)) {
			userGroupDAO.remove(userGroupId);
		}
		if ("AddMember".equals(button)) {
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getUser().getId() != memberId) {
					UserGroup uGroup = new UserGroup();
					uGroup.setGroup(new User());
					uGroup.getGroup().setId(memberId);
					uGroup.setUser(user);
					uGroup.setCreationDate(new Date());
					uGroup.setCreatedBy(permissions.getUserId());
					userGroupDAO.save(uGroup);
				}
			}
			return "member";
		}
		if ("RemoveMember".equals(button)) {
			userGroupDAO.remove(userGroupId);
			return "member";
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

}
