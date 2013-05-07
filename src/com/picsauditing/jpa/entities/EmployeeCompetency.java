package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;

import javax.persistence.*;

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

	@ReportField(type = FieldType.Boolean)
	public boolean isSkilled() {
		return skilled;
	}

	public void setSkilled(boolean skilled) {
		this.skilled = skilled;
	}

	@Transient
	public boolean isMissingDocumentation() {
		boolean missing = false;
		if (competency.isRequiresDocumentation()) {
			boolean found = false;
			for (OperatorCompetencyEmployeeFile employeeFile : employee.getCompetencyFiles()) {
				if (employeeFile.getCompetency().equals(competency) && !employeeFile.isExpired()) {
					found = true;
				}
			}

			if (!found) {
				missing = true;
			}
		}

		return missing;
	}
}
