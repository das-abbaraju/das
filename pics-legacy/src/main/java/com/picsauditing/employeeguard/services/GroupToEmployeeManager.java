package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.Date;
import java.util.List;

public class GroupToEmployeeManager {

	public void updateAccountGroupEmployees(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		List<AccountGroupEmployee> accountGroupEmployee = getLinkedEmployees(groupInDatabase, updatedEmployee, appUserId);
		groupInDatabase.getEmployees().clear();
		groupInDatabase.getEmployees().addAll(accountGroupEmployee);
	}

	private List<AccountGroupEmployee> getLinkedEmployees(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountGroupEmployee> accountGroupEmployees = IntersectionAndComplementProcess.intersection(updatedEmployee.getEmployees(),
				groupInDatabase.getEmployees(), AccountGroupEmployee.COMPARATOR, callback);

		accountGroupEmployees.addAll(callback.getRemovedEntities());

		return accountGroupEmployees;
	}

}
