package com.picsauditing.access;

import com.picsauditing.access.user.UserMode;
import com.picsauditing.access.user.UserModeProvider;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.hierarchy.HierarchyBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class PermissionBuilder {

	@Autowired
	private HierarchyBuilder hierarchyBuilder;
	@Autowired
	private UserModeProvider userModeProvider;
	@Autowired
	private UserDAO dao;

	public Permissions login(User user) throws Exception {
		Permissions permissions = new Permissions();
		permissions.login(user);
		populatePermissionsWithUserInfo(permissions, user);
		build(permissions);

		return permissions;
	}

	public Permissions login(final AppUser appUser, final Identifiable identifiable) {
		Permissions permissions = new Permissions();

		permissions.login(appUser, identifiable);
		permissions.setAvailableUserModes(userModeProvider.getAvailableUserModes(appUser.getId()));
		permissions.setCurrentMode(getCurrentMode(permissions));

		return permissions;
	}

	private UserMode getCurrentMode(final Permissions permissions) {
		if (permissions.getAvailableUserModes().size() == 1) {
			return permissions.getAvailableUserModes().iterator().next();
		}

		return null;
	}

	private void build(Permissions permissions) {
		Set<Integer> groupIDs = getDirectlyRelatedGroupIds(permissions.getUserId());
		Set<Integer> allInheritedGroupIds = hierarchyBuilder.retrieveAllEntityIdsInHierarchy(permissions.getUserId());

		permissions.getAllInheritedGroupIds().clear();
		permissions.getAllInheritedGroupIds().addAll(allInheritedGroupIds);

		permissions.getDirectlyRelatedGroupIds().clear();
		permissions.getDirectlyRelatedGroupIds().addAll(groupIDs);
	}

	private void populatePermissionsWithUserInfo(Permissions permissions, User user) {
		permissions.setUsingVersion7Menus(user.isUsingVersion7Menus());
		permissions.setUsingVersion7MenusDate(user.getUsingVersion7MenusDate());
		permissions.setReportsManagerTutorialDate(user.getReportsManagerTutorialDate());
	}

	private Set<Integer> getDirectlyRelatedGroupIds(final int userID) {
		User user = dao.find(userID);
		Set<Integer> groupIds = new HashSet<Integer>();
		for (UserGroup group : user.getGroups()) {
			groupIds.add(group.getGroup().getId());
		}

		return groupIds;
	}
}
