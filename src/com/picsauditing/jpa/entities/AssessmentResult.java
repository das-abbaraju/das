package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "assessment_result")
public class AssessmentResult extends BaseTable {

	private Employee employee;
	private Account assessmentCenter;
	private Date effectiveDate;
	private Date expirationDate;

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne
	@JoinColumn(name = "assessmentCenterID", nullable = false, updatable = false)
	public Account getAssessmentCenter() {
		return assessmentCenter;
	}

	public void setAssessmentCenter(Account assessmentCenter) {
		this.assessmentCenter = assessmentCenter;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

}
