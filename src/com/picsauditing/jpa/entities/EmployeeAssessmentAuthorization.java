package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_assessment_authorization")
public class EmployeeAssessmentAuthorization extends BaseTable {

	private Employee employee;
	private Account assessmentCenter;
	private String membershipID;
	private String authorizationKey;

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

	public String getMembershipID() {
		return membershipID;
	}

	public void setMembershipID(String membershipID) {
		this.membershipID = membershipID;
	}

	public String getAuthorizationKey() {
		return authorizationKey;
	}

	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

}
