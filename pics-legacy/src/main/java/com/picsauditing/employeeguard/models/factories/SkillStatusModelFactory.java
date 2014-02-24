package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillStatusModelFactory extends SkillModelFactory {

	public Map<Integer, List<SkillStatusModel>> createProjectIdToSkillStatusModelMap(
			final Map<Project, List<AccountSkill>> projectSkillsMap,
			final Map<AccountSkill, SkillStatus> skillStatuses) {

		if (MapUtils.isEmpty(projectSkillsMap)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<SkillStatusModel>> roleIdToSkillsMap = new HashMap<>();
		for (Project project : projectSkillsMap.keySet()) {
			roleIdToSkillsMap.put(project.getId(), new ArrayList<SkillStatusModel>());
			for (AccountSkill accountSkill : projectSkillsMap.get(project)) {
				roleIdToSkillsMap.get(project.getId()).add(create(accountSkill, skillStatuses.get(accountSkill)));
			}
		}

		return roleIdToSkillsMap;
	}

	public Map<Integer, List<SkillStatusModel>> createRoleIdToSkillStatusModelMap(
			final Map<Role, List<AccountSkill>> roleSkillsMap,
			final Map<AccountSkill, SkillStatus> skillStatuses) {

		if (MapUtils.isEmpty(roleSkillsMap)) {
			return Collections.emptyMap();
		}

		Map<Integer, List<SkillStatusModel>> roleIdToSkillsMap = new HashMap<>();
		for (Role role : roleSkillsMap.keySet()) {
			roleIdToSkillsMap.put(role.getId(), new ArrayList<SkillStatusModel>());
			for (AccountSkill accountSkill : roleSkillsMap.get(role)) {
				roleIdToSkillsMap.get(role.getId()).add(create(accountSkill, skillStatuses.get(accountSkill)));
			}
		}

		return roleIdToSkillsMap;
	}

	public List<SkillStatusModel> create(final List<AccountSkill> accountSkills,
	                                     final Map<AccountSkill, SkillStatus> skillStatusMap) {
		if (CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		List<SkillStatusModel> skillStatusModels = new ArrayList<>();
		for (AccountSkill accountSkill : accountSkills) {
			skillStatusModels.add(create(accountSkill, skillStatusMap.get(accountSkill)));
		}

		return skillStatusModels;
	}

	public SkillStatusModel create(final AccountSkill accountSkill, final SkillStatus skillStatus) {
		SkillModel skillModel = super.create(accountSkill);
		SkillStatusModel skillStatusModel = new SkillStatusModel(skillModel);
		skillStatusModel.setStatus(skillStatus);
		return skillStatusModel;
	}
}
