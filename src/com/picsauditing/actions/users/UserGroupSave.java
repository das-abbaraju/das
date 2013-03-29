package com.picsauditing.actions.users;

import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.jpa.entities.UserSwitch;

@SuppressWarnings("serial")
public class UserGroupSave extends UsersManage {
	protected int memberId;
	protected int groupId;
	protected int userGroupId;

    public String execute() throws Exception {
		super.execute();
		user = userDAO.find(user.getId());
		
		
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
			// Remove from memory first
			UserGroup remove = null;
			for (UserGroup ug: user.getGroups())
				if (ug.getId() == userGroupId) {
					remove = ug;
					break;
				}
			user.getGroups().remove(remove);
			// Then remove fom the database
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
				UserSwitch userSwitch = userSwitchDao.findByUserIdAndSwitchToId(memberId, user.getId());

				if (userSwitch != null) {
					addActionError("That user already has the ability to switch to this group.");
				} else {
					userSwitch = new UserSwitch();
					userSwitch.setUser(member);
					userSwitch.setSwitchTo(user);
					userSwitch.setAuditColumns(permissions);
                    userSwitchDao.save(userSwitch);
				}
			}

			return "userswitch";
		}
		if ("RemoveSwitchFrom".equals(button)) {

			if (memberId != 0) {
				UserSwitch userSwitch = userSwitchDao.findByUserIdAndSwitchToId(memberId, user.getId());
				if (userSwitch != null) {
                    userSwitchDao.remove(userSwitch.getId());
				}
			}

			return "userswitch";
		}

		return SUCCESS;
	}

	private boolean addUserToGroup(User user, User group) throws Exception {
        UserGroupManagementStatus status = userManagementService.userIsAddableToGroup(user, group);
        if (!status.isOk) {
            addActionError(status.notOkErrorKey);
            return false;
        } else {
            UserGroup userGroup = userManagementService.addUserToGroup(user, group, permissions);
            addToGroupsForWebDisplay(user, group, userGroup);
            return true;
        }
	}

    private void addToGroupsForWebDisplay(User user, User group, UserGroup userGroup) {
        if (!group.getMembers().contains(userGroup)) {
            group.getMembers().add(userGroup);
        }
        if (!user.getGroups().contains(userGroup)) {
            user.getGroups().add(userGroup);
        }
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
