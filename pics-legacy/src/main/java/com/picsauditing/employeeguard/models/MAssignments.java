package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class MAssignments {
	private Set<MEmployeesManager.MEmployee> totalEmployeesSet;

	@Expose
	private int totalEmployees;

	@Expose
	private MEmployeeRollupStatus employeeRollupStatus;

	private Set<MEmployeesManager.MEmployee> employees;


	public Set<MEmployeesManager.MEmployee> getTotalEmployeesSet() {
		return totalEmployeesSet;
	}

	public void setTotalEmployeesSet(Set<MEmployeesManager.MEmployee> totalEmployeesSet) {
		this.totalEmployeesSet = totalEmployeesSet;
	}

	public int getTotalEmployees() {
		return totalEmployees;
	}

	public void setTotalEmployees(int totalEmployees) {
		this.totalEmployees = totalEmployees;
	}

	public MEmployeeRollupStatus getEmployeeRollupStatus() {
		return employeeRollupStatus;
	}

	public void setEmployeeRollupStatus(MEmployeeRollupStatus employeeRollupStatus) {
		this.employeeRollupStatus = employeeRollupStatus;
	}

	public Set<MEmployeesManager.MEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(Set<MEmployeesManager.MEmployee> employees) {
		this.employees = employees;
	}
}
