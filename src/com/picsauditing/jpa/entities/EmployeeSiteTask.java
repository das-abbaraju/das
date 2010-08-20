package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_site_task")
public class EmployeeSiteTask extends BaseTable {
	private EmployeeSite employeeSite;
	private JobTask task;
	
	@ManyToOne
	@JoinColumn(name = "employeeSiteID", nullable = false, updatable = false)
	public EmployeeSite getEmployeeSite() {
		return employeeSite;
	}
	
	public void setEmployeeSite(EmployeeSite employeeSite) {
		this.employeeSite = employeeSite;
	}
	
	@ManyToOne
	@JoinColumn(name = "taskID", nullable = false, updatable = false)
	public JobTask getTask() {
		return task;
	}
	
	public void setTask(JobTask task) {
		this.task = task;
	}
}
