package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_competency")
public class EmployeeCompetency extends BaseTable {

	private Employee employee;
	private OperatorCompetency competency;
	private boolean skilled;

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne
	@JoinColumn(name = "competencyID", nullable = false, updatable = false)
	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	public boolean isSkilled() {
		return skilled;
	}

	public void setSkilled(boolean skilled) {
		this.skilled = skilled;
	}

}
