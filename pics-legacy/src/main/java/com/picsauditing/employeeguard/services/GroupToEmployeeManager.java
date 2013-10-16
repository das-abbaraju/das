package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.Date;
import java.util.List;

public class GroupToEmployeeManager {

	public void updateAccountGroupEmployees(final AccountGroup accountGroupInDatabase, final AccountGroup updatedEmployee, final int appUserId) {
		List<AccountGroupEmployee> accountGroupEmployee = getLinkedEmployees(accountGroupInDatabase, updatedEmployee, appUserId);
		accountGroupInDatabase.getEmployees().clear();
		accountGroupInDatabase.getEmployees().addAll(accountGroupEmployee);
	}

	private List<AccountGroupEmployee> getLinkedEmployees(final AccountGroup accountGroupInDatabase, final AccountGroup updatedEmployee, final int appUserId) {
        BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountGroupEmployee> accountGroupEmployees = IntersectionAndComplementProcess.intersection(updatedEmployee.getEmployees(),
				accountGroupInDatabase.getEmployees(), AccountGroupEmployee.COMPARATOR, callback);

		accountGroupEmployees.addAll(callback.getRemovedEntities());

		return accountGroupEmployees;
	}

}
