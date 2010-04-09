package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "account_employee")
public class AccountEmployee extends BaseTable {
	private Account account;
	private Employee employee;
	private EmployeeStatus status;
	private EmployeeClassification classification;

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false, updatable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Enumerated(EnumType.STRING)
	public EmployeeStatus getStatus() {
		return status;
	}

	public void setStatus(EmployeeStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public EmployeeClassification getClassification() {
		return classification;
	}

	public void setClassification(EmployeeClassification classification) {
		this.classification = classification;
	}

}
