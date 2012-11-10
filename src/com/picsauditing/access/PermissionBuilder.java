package com.picsauditing.access;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class PermissionBuilder {
	@Autowired
	private HierarchyBuilder hierarchyBuilder;
	@Autowired
	private FeatureToggle featureToggle;
	@Autowired
	private UserDAO dao;
	
	public Permissions login(User user) throws Exception {
		Permissions permissions = new Permissions();
		permissions.login(user);
		LocaleController.setLocaleOfNearestSupported(permissions);
		build(permissions);
		return permissions;
	}

	public void build(Permissions permissions) {
		Set<Integer> groupIDs;
		if (useInheritedGroups()) {
			groupIDs = hierarchyBuilder.retrieveAllEntityIdsInHierarchy(permissions.getUserId());
		} else {
			// TODO remove this section after we're able to finish the testing
			// on Group Inheritance
			groupIDs = getDirectlyRelatedGroupIds(permissions.getUserId());
		}
		
		permissions.getAllInheritedGroupIds().clear();
		permissions.getAllInheritedGroupIds().addAll(groupIDs);
	}

	private boolean useInheritedGroups() {
		return featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_PERMISSION_GROUPS);
	}

	private Set<Integer> getDirectlyRelatedGroupIds(int userID) {
		User user = dao.find(userID);
		Set<Integer> groupIds = new HashSet<Integer>();
		for (UserGroup group : user.getGroups()) {
			groupIds.add(group.getGroup().getId());
		}

		return groupIds;
	}
}
