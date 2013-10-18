package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import org.apache.commons.lang3.ArrayUtils;

public class EmployeeBuilder {

	private final Employee employee;

	public EmployeeBuilder() {
		employee = new Employee();
	}
	
	public EmployeeBuilder(int id, int accountId) {
		employee = new Employee(id, accountId);
	}
	
	public EmployeeBuilder id(int id) {
		employee.setId(id);
		return this;
	}

	public EmployeeBuilder accountId(int accountId) {
		employee.setAccountId(accountId);
		return this;
	}
	
	public EmployeeBuilder firstName(String firstName) {
		employee.setFirstName(firstName);
		return this;
	}

	public EmployeeBuilder lastName(String lastName) {
		employee.setLastName(lastName);
		return this;
	}

	public EmployeeBuilder email(String email) {
		employee.setEmail(email);
		return this;
	}

	public EmployeeBuilder phoneNumber(String phoneNumber) {
		employee.setPhone(phoneNumber);
		return this;
	}

	public EmployeeBuilder slug(String slug) {
		employee.setSlug(slug);
		return this;
	}

	public EmployeeBuilder positionName(String positionName) {
		employee.setPositionName(positionName);
		return this;
	}

	public EmployeeBuilder groups(String[] groups) {
		if (ArrayUtils.isEmpty(groups)) {
			return this;
		}
		
		employee.getGroups().clear();

		for (String groupName : groups) {
			AccountGroup accountGroup = new AccountGroup();
			accountGroup.setAccountId(employee.getAccountId());
			accountGroup.setName(groupName);
			
			AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, accountGroup);
			employee.getGroups().add(accountGroupEmployee);
		}

		return this;
	}

	public Employee build() {
		return employee;
	}

}
