package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class RoleModelFactory {

	public Map<Integer, List<RoleModel>> createProjectIdToRoleModelMap(final List<Project> projects,
																	   final Map<Project, List<Role>> projectRoles,
																	   final Map<Integer, List<SkillModel>> roleIdToSkillModelMap) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<RoleModel>> projectIdToRoleModelMap = new HashMap<>();
		for (Project project : projects) {
			projectIdToRoleModelMap.put(project.getId(), new ArrayList<RoleModel>());
			addRolesToProject(projectRoles, roleIdToSkillModelMap, projectIdToRoleModelMap, project);
		}

		return projectIdToRoleModelMap;
	}

	private void addRolesToProject(final Map<Project, List<Role>> projectRoles,
								   final Map<Integer, List<SkillModel>> roleIdToSkillModelMap,
								   final Map<Integer, List<RoleModel>> projectIdToRoleModelMap,
								   final Project project) {
		if (CollectionUtils.isNotEmpty(projectRoles.get(project))) {
			for (Role role : projectRoles.get(project)) {
				projectIdToRoleModelMap.get(project).add(create(role, roleIdToSkillModelMap.get(role.getId())));
			}
		}
	}

	public List<RoleModel> create(final List<Role> roles, final Map<Integer, List<SkillModel>> roleIdToSkillsMap) {
		if (CollectionUtils.isEmpty(roles)) {
			return Collections.emptyList();
		}

		List<RoleModel> roleModels = new ArrayList<>();
		for (Role role : roles) {
			roleModels.add(create(role, roleIdToSkillsMap.get(role.getId())));
		}

		return roleModels;
	}

	public RoleModel create(final Role role, final List<? extends SkillModel> skills) {
		RoleModel roleModel = new RoleModel();
		roleModel.setId(role.getId());
		roleModel.setName(role.getName());
		roleModel.setSkills(skills);
		return roleModel;
	}
}
