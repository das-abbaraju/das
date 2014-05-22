package com.picsauditing.securitysession;

import com.picsauditing.securitysession.entities.AppUser;
import com.picsauditing.securitysession.entities.User;
import com.picsauditing.securitysession.entities.UserGroup;
import com.picsauditing.securitysession.service.UserService;
import com.picsauditing.securitysession.user.UserMode;
import com.picsauditing.securitysession.user.UserModeProvider;
import com.picsauditing.securitysession.util.hierarchy.HierarchyBuilder;
import com.picsauditing.database.domain.Identifiable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class PermissionBuilder {

	@Autowired
	private HierarchyBuilder hierarchyBuilder;
	@Autowired
	private UserModeProvider userModeProvider;
	@Autowired
	private UserService userService;

	public Permissions login(User user) throws Exception {
		Permissions permissions = new Permissions();
		permissions.login(user);
		populatePermissionsWithUserInfo(permissions, user);
		build(permissions);

		addUserModeInfo(permissions, user.getAppUser().getId());

		return permissions;
	}

	public Permissions employeeUserLogin(final AppUser appUser, final Identifiable identifiable) {
		Permissions permissions = new Permissions();

		permissions.login(appUser, identifiable);

		int appUserId = appUser.getId();

		addAvailableModes(permissions, appUserId);
		permissions.setCurrentMode(UserMode.EMPLOYEE);

		return permissions;
	}

	private void addUserModeInfo(final Permissions permissions, final int appUserId) {
		addAvailableModes(permissions, appUserId);
		permissions.setCurrentMode(userModeProvider.getCurrentUserMode(permissions));
	}

	private void addAvailableModes(final Permissions permissions, final int appUserId) {
		permissions.setAvailableUserModes(userModeProvider.getAvailableUserModes(appUserId));
	}

	private void build(final Permissions permissions) {
		Set<Integer> groupIDs = getDirectlyRelatedGroupIds(permissions.getUserId());
		Set<Integer> allInheritedGroupIds = hierarchyBuilder.retrieveAllEntityIdsInHierarchy(permissions.getUserId());

		permissions.getAllInheritedGroupIds().clear();
		permissions.getAllInheritedGroupIds().addAll(allInheritedGroupIds);

		permissions.getDirectlyRelatedGroupIds().clear();
		permissions.getDirectlyRelatedGroupIds().addAll(groupIDs);
	}

	private void populatePermissionsWithUserInfo(final Permissions permissions, final User user) {
		permissions.setUsingVersion7Menus(user.isUsingVersion7Menus());
		permissions.setUsingVersion7MenusDate(user.getUsingVersion7MenusDate());
		permissions.setReportsManagerTutorialDate(user.getReportsManagerTutorialDate());
	}

	private Set<Integer> getDirectlyRelatedGroupIds(final int userId) {
		User user = userService.findById(userId);
		Set<Integer> groupIds = new HashSet<>();
		for (UserGroup group : user.getGroups()) {
			groupIds.add(group.getGroup().getId());
		}

		return groupIds;
	}
}
