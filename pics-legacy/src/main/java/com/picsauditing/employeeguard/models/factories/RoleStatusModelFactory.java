package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class RoleStatusModelFactory extends RoleModelFactory {

	public Map<Integer, List<RoleStatusModel>> createProjectIdToRoleModelMap(final Collection<Project> projects,
	                                                                         final Map<Project, Set<Role>> projectRoles,
	                                                                         final Map<Integer, List<SkillStatusModel>> roleIdToSkillModelMap,
	                                                                         final Map<Role, SkillStatus> roleStatuses) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<RoleStatusModel>> projectIdToRoleModelMap = new HashMap<>();
		for (Project project : projects) {
			projectIdToRoleModelMap.put(project.getId(), new ArrayList<RoleStatusModel>());
			addRolesToProject(projectRoles, roleIdToSkillModelMap, projectIdToRoleModelMap, project, roleStatuses);
		}

		return projectIdToRoleModelMap;
	}

	private void addRolesToProject(final Map<Project, ? extends Collection<Role>> projectRoles,
	                               final Map<Integer, List<SkillStatusModel>> roleIdToSkillModelMap,
	                               final Map<Integer, List<RoleStatusModel>> projectIdToRoleModelMap,
	                               final Project project,
	                               final Map<Role, SkillStatus> roleStatuses) {

		if (CollectionUtils.isNotEmpty(projectRoles.get(project))) {
			for (Role role : projectRoles.get(project)) {
				projectIdToRoleModelMap.get(project.getId()).add(this.create(
						role,
						roleIdToSkillModelMap.get(role.getId()),
						roleStatuses.get(role)
				));
			}
		}
	}

	public List<RoleStatusModel> create(final Collection<Role> roles,
	                                    final Map<Integer, List<SkillStatusModel>> roleSkills,
	                                    final Map<Role, SkillStatus> roleStatuses) {

		if (CollectionUtils.isEmpty(roles)) {
			return Collections.emptyList();
		}

		List<RoleStatusModel> roleStatusModels = new ArrayList<>();
		for (Role role : roles) {
			roleStatusModels.add(create(role, roleSkills.get(role.getId()), roleStatuses.get(role)));
		}

		return roleStatusModels;
	}

	public RoleStatusModel create(final Role role, final List<SkillStatusModel> skills, final SkillStatus status) {
		RoleStatusModel roleStatusModel = new RoleStatusModel();
		roleStatusModel.setId(role.getId());
		roleStatusModel.setName(role.getName());
		roleStatusModel.setSkills(skills);
		roleStatusModel.setStatus(status);
		return roleStatusModel;
	}

}
