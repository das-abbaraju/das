package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_qualification")
public class EmployeeQualification extends BaseTable {

	private Employee employee;
	private JobTask task;
	private boolean qualified;

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne
	@JoinColumn(name = "taskID", nullable = false, updatable = false)
	public JobTask getTask() {
		return task;
	}

	public void setTask(JobTask task) {
		this.task = task;
	}

	public boolean isQualified() {
		return qualified;
	}

	public void setQualified(boolean qualified) {
		this.qualified = qualified;
	}

}
