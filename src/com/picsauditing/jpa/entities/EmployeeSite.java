package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_site")
@PrimaryKeyJoinColumn(name = "id")
public class EmployeeSite extends BaseTable implements JSONable {

	private OperatorAccount operator;
	private JobSite jobSite;
	private Employee employee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "employeeID", nullable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name = "jobSiteID", nullable = true)
	public JobSite getJobSite() {
		return jobSite;
	}

	public void setJobSite(JobSite jobSite) {
		this.jobSite = jobSite;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("operator", operator.toJSON());
		json.put("jobSite", jobSite.toJSON());
		json.put("employee", employee.toJSON());

		return json;
	}
}
