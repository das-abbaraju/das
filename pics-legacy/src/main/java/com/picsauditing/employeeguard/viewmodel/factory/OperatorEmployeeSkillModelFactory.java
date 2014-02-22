package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;

import java.util.*;

public class OperatorEmployeeSkillModelFactory {

	public List<OperatorEmployeeSkillModel> create(final Map<AccountSkill, SkillStatus> skillStatusMap,
												   final Collection<AccountSkill> siteSkills,
												   final Collection<Role> roles) {
		List<OperatorEmployeeSkillModel> skills = new ArrayList<>();
		for (AccountSkill skill : skillStatusMap.keySet()) {
			skills.add(create(skill, skillStatusMap.get(skill), siteSkills, roles));
		}

		return skills;
	}

	public OperatorEmployeeSkillModel create(final AccountSkill accountSkill,
											 final SkillStatus skillStatus,
											 final Collection<AccountSkill> siteSkills,
											 final Collection<Role> roles) {
		Set<Integer> projectIds = new HashSet<>(Utilities.getIdsFromCollection(accountSkill.getProjects(),
				new Utilities.Identitifable<ProjectSkill, Integer>() {
					@Override
					public Integer getId(ProjectSkill element) {
						return element.getProject().getId();
					}
				}));

		Set<Integer> roleIds;
		if (siteSkills.contains(accountSkill)) {
			roleIds = Utilities.getIdsFromCollection(roles,
					new Utilities.Identitifable<Role, Integer>() {
						@Override
						public Integer getId(Role element) {
							return element.getId();
						}
					});
		} else {
			roleIds = Utilities.getIdsFromCollection(accountSkill.getRoles(),
					new Utilities.Identitifable<AccountSkillRole, Integer>() {
						@Override
						public Integer getId(AccountSkillRole element) {
							return element.getRole().getId();
						}
					});
		}

		for (AccountSkillRole accountSkillRole : accountSkill.getRoles()) {
			for (ProjectRole projectRole : accountSkillRole.getRole().getProjects()) {
				projectIds.add(projectRole.getProject().getId());
			}
		}

		return new OperatorEmployeeSkillModel.Builder()
				.id(accountSkill.getId())
				.name(accountSkill.getName())
				.projects(projectIds)
				.roles(roleIds)
				.status(skillStatus)
				.build();
	}

}
