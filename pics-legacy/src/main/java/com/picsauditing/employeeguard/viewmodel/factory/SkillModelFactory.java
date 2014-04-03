package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.SkillModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SkillModelFactory {

	public List<SkillModel> create(List<AccountSkill> accountSkills, int totalEmployeesForAccount) {
		if (CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		List<SkillModel> skillModels = new ArrayList<>();
		for (AccountSkill accountSkill : accountSkills) {
			skillModels.add(create(accountSkill, totalEmployeesForAccount));
		}

		return skillModels;
	}

	public SkillModel create(AccountSkill accountSkill, int totalEmployeesForAccount) {
		SkillModel skillModel = new SkillModel();
		skillModel.setId(accountSkill.getId());
		skillModel.setName(accountSkill.getName());
		skillModel.setRuleType(accountSkill.getRuleType());
		skillModel.setNumberOfEmployees(getNumberOfEmployeesForSkill(accountSkill, totalEmployeesForAccount));
		skillModel.setGroups(accountSkill.getGroups());
		return skillModel;
	}

	public int getNumberOfEmployeesForSkill(AccountSkill accountSkill, int totalEmployeesForAccount) {
		if (accountSkill.getRuleType() != null && accountSkill.getRuleType().isRequired()) {
			return totalEmployeesForAccount;
		}

		return getEmployeeCount(accountSkill.getGroups());
	}

	private int getEmployeeCount(List<AccountSkillGroup> accountSkillGroups) {
		Set<Integer> employeeIds = new HashSet<>();
		for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
			employeeIds.addAll(PicsCollectionUtil.getIdsFromCollection(accountSkillGroup.getGroup().getEmployees(), new PicsCollectionUtil.Identitifable<GroupEmployee, Integer>() {

				@Override
				public Integer getId(GroupEmployee groupEmployee) {
					return groupEmployee.getEmployee().getId();
				}
			}));
		}


		return employeeIds.size();
	}
}
