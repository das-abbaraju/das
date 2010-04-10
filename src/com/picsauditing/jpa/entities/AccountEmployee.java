package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.json.simple.JSONObject;

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

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("account", account.toJSON(full));
		json.put("employee", employee.toJSON(full));
		json.put("status", status == null ? null : status.toString());
		json.put("classification", classification == null ? null : classification.toString());

		return json;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AccountEmployee))
			return false;

		AccountEmployee accountEmployee = (AccountEmployee) obj;

		return accountEmployee.getAccount().equals(account) && accountEmployee.getEmployee().equals(employee);
	}

}
