package com.picsauditing.service.fileimport;

import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;

import java.util.*;

class EmployeeDuplicateResolver {
	private EmployeeDAO employeeDAO;

	private List<Employee> duplicateEmployees = Collections.emptyList();
	private List<Employee> uniqueEmployees = Collections.emptyList();
	private Set<Employee> employees;

	public EmployeeDuplicateResolver(final Set<Employee> employees, final EmployeeDAO employeeDAO) {
		this.employees = Collections.unmodifiableSet(employees);
		this.employeeDAO = employeeDAO;
	}

	public void resolveDuplicates() {
		if (employees != null && !employees.isEmpty()) {
			List<Employee> existingEmployees = employeeDAO.findByFirstNameLastNameAndAccount(employees);

			if (existingEmployees != null && !existingEmployees.isEmpty()) {
				duplicateEmployees = new ArrayList<>();
				uniqueEmployees = new ArrayList<>(employees);

				for (Employee existingEmployee : existingEmployees) {
					for (Employee importedEmployee : employees) {
						copyEmployeeSitesToExistingEmployee(existingEmployee, importedEmployee);
					}
				}

				uniqueEmployees.removeAll(duplicateEmployees);
			}
		}
	}

	private void copyEmployeeSitesToExistingEmployee(Employee existingEmployee, Employee importedEmployee) {
		if (existingEmployee.equals(importedEmployee)) {
			Set<EmployeeSite> allSites = new HashSet<>(existingEmployee.getEmployeeSites());
			allSites.addAll(importedEmployee.getEmployeeSites());

			for (EmployeeSite employeeSite : allSites) {
				employeeSite.setEmployee(existingEmployee);
			}

			existingEmployee.setEmployeeSites(new ArrayList<>(allSites));
			duplicateEmployees.add(existingEmployee);
		}
	}

	public List<Employee> getDuplicateEmployees() {
		return Collections.unmodifiableList(duplicateEmployees);
	}

	public List<Employee> getUniqueEmployees() {
		return Collections.unmodifiableList(uniqueEmployees);
	}
}
