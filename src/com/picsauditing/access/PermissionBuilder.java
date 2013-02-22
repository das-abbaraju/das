package com.picsauditing.access;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.model.i18n.LanguageModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class PermissionBuilder {

	@Autowired
	private HierarchyBuilder hierarchyBuilder;
	@Autowired
	private FeatureToggle featureToggle;
	@Autowired
	private LanguageModel languageModel;
	@Autowired
	private UserDAO dao;

	public Permissions login(User user) throws Exception {
		Permissions permissions = new Permissions(languageModel);
		permissions.login(user);
		build(permissions);
		return permissions;
	}

	public void build(Permissions permissions) {
		Set<Integer> groupIDs = getDirectlyRelatedGroupIds(permissions.getUserId());
		Set<Integer> allInheritedGroupIds = hierarchyBuilder.retrieveAllEntityIdsInHierarchy(permissions.getUserId());

		// This is just for safety, in case something breaks we don't find
		if (!useInheritedGroups()) {
			allInheritedGroupIds = groupIDs;
		}

		permissions.getAllInheritedGroupIds().clear();
		permissions.getAllInheritedGroupIds().addAll(allInheritedGroupIds);

		permissions.getDirectlyRelatedGroupIds().clear();
		permissions.getDirectlyRelatedGroupIds().addAll(groupIDs);
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
