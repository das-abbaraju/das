package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.Date;
import java.util.List;

public class GroupToSkillManager {

	public void updateAccountSkillGroups(final AccountGroup accountGroupInDatabase, final AccountGroup updatedEmployee, final int appUserId) {
		List<AccountSkillGroup> accountSkillGroups = getLinkedSkills(accountGroupInDatabase, updatedEmployee, appUserId);
		accountGroupInDatabase.getSkills().clear();
		accountGroupInDatabase.getSkills().addAll(accountSkillGroups);
	}

	private List<AccountSkillGroup> getLinkedSkills(final AccountGroup accountGroupInDatabase, final AccountGroup updatedEmployee, final int appUserId) {
        BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(updatedEmployee.getSkills(),
				accountGroupInDatabase.getSkills(), AccountSkillGroup.COMPARATOR, callback);

		accountSkillGroups.addAll(callback.getRemovedEntities());

		return accountSkillGroups;
	}
}
