package com.picsauditing.actions.users;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserAccessDAO;
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

	public UserGroupSave(AccountDAO accountDao, OperatorAccountDAO operatorDao, UserDAO userDAO,
			UserAccessDAO userAccessDAO, UserGroupDAO userGroupDAO, UserSwitchDAO userSwitchDAO) {
		super(accountDao, operatorDao, userDAO, userAccessDAO);
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
			User newGroup = userDAO.find(groupId);
			addUserToGroup(user, newGroup);
			return SUCCESS;
		}
		if ("RemoveGroup".equals(button)) {
			userGroupDAO.remove(userGroupId);
			return SUCCESS;
		}
		if ("AddMember".equals(button)) {
			User newUser = userDAO.find(memberId);
			addUserToGroup(newUser, user);
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

	private boolean addUserToGroup(User user, User group) {
		if (!group.isGroup()) {
			addActionError("You can only inherit permissions from groups");
			return false;
		}
		if (user.equals(group)) {
			addActionError("You can't add a group to itself");
			return false;
		}

		for (UserGroup userGroup : user.getGroups()) {
			if (userGroup.getGroup().equals(group)) {
				// Don't add the same group twice
				return false;
			}
		}

		if (user.isGroup()) {
			// Make sure the new parent group isn't a descendant of this child group
			if (containsMember(user, group)) {
				addActionError(group.getName() + " is a descendant of " + user.getName()
						+ ". This action would create an infinite loop.");
				return false;
			}
		}

		// Make sure user isn't in member's member list
		UserGroup uGroup = new UserGroup();

		uGroup.setUser(user);
		uGroup.setGroup(group);
		uGroup.setAuditColumns(permissions);
		userGroupDAO.save(uGroup);
		if (!group.getMembers().contains(uGroup))
			group.getMembers().add(uGroup);
		if (!user.getGroups().contains(uGroup))
			user.getGroups().add(uGroup);

		return true;
	}

	/**
	 * Is the group a child/descendant(member) of the user
	 * 
	 * @param user
	 * @param group
	 * @return
	 */
	private boolean containsMember(User user, User group) {
		for (UserGroup userMember : user.getMembers()) {
			if (userMember.getUser().equals(group)) {
				return true;
			}
			if (containsMember(userMember.getUser(), group))
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
