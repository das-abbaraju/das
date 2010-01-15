package com.picsauditing.actions.users;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserSwitch;

@SuppressWarnings("serial")
public class UserGroupSave extends UsersManage {
	protected int memberId;
	protected int groupId;
	protected int userGroupId;
	protected UserGroupDAO userGroupDAO;
	protected UserSwitchDAO userSwitchDAO;

	public UserGroupSave(AccountDAO accountDao, OperatorAccountDAO operatorDao, UserDAO userDAO, UserGroupDAO userGroupDAO,
			UserSwitchDAO userSwitchDAO) {
		super(accountDao, operatorDao, userDAO);
		this.userGroupDAO = userGroupDAO;
		this.userSwitchDAO = userSwitchDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin()) {
			addActionError("Timeout: you need to login again");
			return LOGIN;
		}
		super.execute();
		if (user == null) {
			addActionError("user is not set");
			return SUCCESS;
		}

		if ("AddGroup".equals(button)) {
			boolean hasUserGroup = false;
			for (UserGroup userGroup : user.getGroups()) {
				if (userGroup.getGroup().getId() == groupId)
					hasUserGroup = true;
			}
			if (!hasUserGroup) {
				UserGroup uGroup = new UserGroup();
				User newGroup = userDAO.find(groupId);
				if (containsGroup(user, newGroup)) {
					this.addActionError("You can't add a circular relationship");
					return SUCCESS;
				}

				uGroup.setGroup(newGroup);
				uGroup.setUser(user);
				uGroup.setAuditColumns(permissions);
				userGroupDAO.save(uGroup);
				user.getGroups().add(uGroup);
			}
		}
		if ("RemoveGroup".equals(button)) {
			userGroupDAO.remove(userGroupId);
		}
		if ("AddMember".equals(button)) {
			// Make sure memberId isn't already in user's member list
			boolean hasUserGroup = false;
			for (UserGroup userGroup : user.getMembers()) {
				if (userGroup.getGroup().getId() == memberId)
					hasUserGroup = true;
			}
			if (!hasUserGroup) {
				// Make sure user isn't in member's member list
				UserGroup uGroup = new UserGroup();
				User newUser = userDAO.find(memberId);
				if (containsMember(newUser, user)) {
					this.addActionError("You can't add a circular relationship");
					return "member";
				}

				uGroup.setUser(newUser);
				uGroup.setGroup(user);
				uGroup.setAuditColumns(permissions);
				userGroupDAO.save(uGroup);
				user.getMembers().add(uGroup);
			}
			return "member";
		}
		if ("RemoveMember".equals(button)) {
			userGroupDAO.remove(userGroupId);
			return "member";
		}
		if ("AddSwitchFrom".equals(button)) {
			User member = userDAO.find(memberId);

			if (member != null) {
				UserSwitch userSwitch = userSwitchDAO.findByUserIdAndSwitchToId(memberId, user.getId());

				if (userSwitch != null) {
					addActionError("That user already has the ability to switch to this group.");
				} else {
					userSwitch = new UserSwitch();
					userSwitch.setUser(member);
					userSwitch.setSwitchTo(user);
					userSwitch.setAuditColumns(permissions);
					userSwitchDAO.save(userSwitch);
				}
			}

			return "userswitch";
		}
		if ("RemoveSwitchFrom".equals(button)) {

			if (memberId != 0) {
				UserSwitch userSwitch = userSwitchDAO.findByUserIdAndSwitchToId(memberId, user.getId());
				if (userSwitch != null) {
					userSwitchDAO.remove(userSwitch.getId());
				}
			}

			return "userswitch";
		}

		return SUCCESS;
	}

	private boolean containsMember(User group, User member) {
		for (UserGroup userMember : group.getMembers()) {
			if (userMember.getUser().getId() == member.getId()) {
				return true;
			}
			if (containsMember(userMember.getUser(), member))
				return true;
		}
		return false;
	}

	private boolean containsGroup(User user, User group) {
		for (UserGroup userGroup : user.getGroups()) {
			if (userGroup.getGroup().getId() == group.getId()) {
				return true;
			}
			if (containsGroup(userGroup.getGroup(), group))
				return true;
		}
		return false;
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
