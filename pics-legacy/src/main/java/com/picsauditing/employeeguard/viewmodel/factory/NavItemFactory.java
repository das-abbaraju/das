package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.operator.NavItem;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NavItemFactory {

	public List<NavItem> createForRoles(final Map<Role, SkillStatus> roleStatusMap) {
		if (MapUtils.isEmpty(roleStatusMap)) {
			return Collections.emptyList();
		}

		List<NavItem> navItems = new ArrayList<>();
		for (Role role : roleStatusMap.keySet()) {
			navItems.add(create(role, roleStatusMap.get(role)));
		}

		return navItems;
	}

	public List<NavItem> createForProjects(final Map<Project, SkillStatus> projectStatusMap) {
		if (MapUtils.isEmpty(projectStatusMap)) {
			return Collections.emptyList();
		}

		List<NavItem> navItems = new ArrayList<>();
		for (Project project : projectStatusMap.keySet()) {
			navItems.add(create(project, projectStatusMap.get(project)));
		}

		return navItems;
	}

	public NavItem create(final Role role, SkillStatus skillStatus) {
		return new NavItem.Builder()
				.id(role.getId())
				.name(role.getName())
				.build();
	}

	public NavItem create(final Project project, SkillStatus skillStatus) {
		return new NavItem.Builder()
				.id(project.getId())
				.name(project.getName())
				.build();
	}
}
