package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;

public class EmployeeEmploymentForm implements DuplicateInfoProvider {

	private String title;
	private String employeeId;
	private String[] groups;

	public EmployeeEmploymentForm() {
	}

	public EmployeeEmploymentForm(final Employee employee) {
		this.title = employee.getPositionName();
		this.employeeId = employee.getSlug();
		this.groups = getGroupNames(employee);
	}

	private String[] getGroupNames(final Employee employee) {
		String[] groupNames = new String[employee.getGroups().size()];

		for (int index = 0; index < groupNames.length; index++) {
			groupNames[index] = employee.getGroups().get(index).getGroup().getName();
		}

		return groupNames;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	@Override
	public UniqueIndexable getUniqueIndexable() {
		return new Employee.EmployeeAccountSlugUniqueKey(SessionInfoProviderFactory.getSessionInfoProvider().getId(),
				SessionInfoProviderFactory.getSessionInfoProvider().getAccountId(), employeeId);
	}

	@Override
	public Class<?> getType() {
		return Employee.class;
	}
}
