package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.Date;
import java.util.List;

public class GroupToSkillManager {

	public void updateAccountSkillGroups(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		List<AccountSkillGroup> accountSkillGroups = getLinkedSkills(groupInDatabase, updatedEmployee, appUserId);
		groupInDatabase.getSkills().clear();
		groupInDatabase.getSkills().addAll(accountSkillGroups);
	}

	private List<AccountSkillGroup> getLinkedSkills(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(updatedEmployee.getSkills(),
				groupInDatabase.getSkills(), AccountSkillGroup.COMPARATOR, callback);

		accountSkillGroups.addAll(callback.getRemovedEntities());

		return accountSkillGroups;
	}
}
