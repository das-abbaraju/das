package com.picsauditing.employeeguard.models;

import java.util.List;

public class CompanyModel implements Identifiable, Nameable {

	private int id;
	private String name;
	private String title;
	private List<CompanyEmployeeModel> employees;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<CompanyEmployeeModel> getEmployees() {
		return employees;
	}

	public void setEmployees(List<CompanyEmployeeModel> employees) {
		this.employees = employees;
	}
}
