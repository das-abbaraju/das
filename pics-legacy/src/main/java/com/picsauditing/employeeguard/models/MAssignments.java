package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

public class MAssignments {
	private Set<MContractorEmployeeManager.MContractorEmployee> totalEmployeesSet;

	@Expose
	private Integer totalEmployees;

	@Expose
	private MEmployeeRollupStatus employeeRollupStatus;

	@Expose
	private Set<MContractorEmployeeManager.MContractorEmployee> employees;


	//-- Getters/Setters


	public Set<MContractorEmployeeManager.MContractorEmployee> getTotalEmployeesSet() {
		return totalEmployeesSet;
	}

	public void addToTotalEmployees(MContractorEmployeeManager.MContractorEmployee mContractorEmployee) {
		this.totalEmployeesSet.add(mContractorEmployee);
	}

	public Set<MContractorEmployeeManager.MContractorEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(Set<MContractorEmployeeManager.MContractorEmployee> employees) {
		this.employees = employees;
	}

	public Integer getTotalEmployees() {
		return totalEmployees;
	}

	public void updateTotalEmployeesCount(){
		this.totalEmployees = totalEmployeesSet.size();
	}

	public MEmployeeRollupStatus getEmployeeRollupStatus() {
		return employeeRollupStatus;
	}

	public void setEmployeeRollupStatus(MEmployeeRollupStatus employeeRollupStatus) {
		this.employeeRollupStatus = employeeRollupStatus;
	}

	public void setTotalEmployeesSet(Set<MContractorEmployeeManager.MContractorEmployee> totalEmployeesSet) {
		this.totalEmployeesSet = totalEmployeesSet;
	}
}
