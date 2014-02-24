package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.SkillModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillModelFactory {

	public Map<Integer, List<SkillModel>> createProjectIdToSkillModelMap(final Map<Project, List<AccountSkill>> projectSkillsMap) {
		if (MapUtils.isEmpty(projectSkillsMap)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<SkillModel>> roleIdToSkillsMap = new HashMap<>();
		for (Project project : projectSkillsMap.keySet()) {
			roleIdToSkillsMap.put(project.getId(), create(projectSkillsMap.get(project)));
		}

		return roleIdToSkillsMap;
	}

	public Map<Integer, List<SkillModel>> createRoleIdToSkillModelMap(final Map<Role, List<AccountSkill>> roleSkillsMap) {
		if (MapUtils.isEmpty(roleSkillsMap)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<SkillModel>> roleIdToSkillsMap = new HashMap<>();
		for (Role role : roleSkillsMap.keySet()) {
			roleIdToSkillsMap.put(role.getId(), create(roleSkillsMap.get(role)));
		}

		return roleIdToSkillsMap;
	}

	public List<SkillModel> create(final List<AccountSkill> accountSkills) {
		if (CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		List<SkillModel> skillModels = new ArrayList<>();
		for (AccountSkill accountSkill : accountSkills) {
			skillModels.add(create(accountSkill));
		}

		return skillModels;
	}

	public SkillModel create(final AccountSkill skill) {
		SkillModel skillModel = new SkillModel();
		skillModel.setId(skill.getId());
		skillModel.setName(skill.getName());
		return skillModel;
	}
}
