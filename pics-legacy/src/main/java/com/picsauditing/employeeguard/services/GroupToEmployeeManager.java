package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;

import java.util.Date;
import java.util.List;

public class GroupToEmployeeManager {

	public void updateAccountGroupEmployees(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		List<GroupEmployee> groupEmployee = getLinkedEmployees(groupInDatabase, updatedEmployee, appUserId);
		groupInDatabase.getEmployees().clear();
		groupInDatabase.getEmployees().addAll(groupEmployee);
	}

	private List<GroupEmployee> getLinkedEmployees(final Group groupInDatabase, final Group updatedEmployee, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<GroupEmployee> groupEmployees = IntersectionAndComplementProcess.intersection(updatedEmployee.getEmployees(),
				groupInDatabase.getEmployees(), GroupEmployee.COMPARATOR, callback);

		groupEmployees.addAll(callback.getRemovedEntities());

		return groupEmployees;
	}

}
