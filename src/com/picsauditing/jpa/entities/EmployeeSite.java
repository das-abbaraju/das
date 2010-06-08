package com.picsauditing.jpa.entities;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_site")
@PrimaryKeyJoinColumn(name = "id")
public class EmployeeSite extends BaseHistory implements JSONable {

	private OperatorAccount operator;
	private JobSite jobSite;
	private Employee employee;

	private Date orientationDate;
	private Date orientationExpiration;

	private Date effectiveDate;
	private Date expirationDate;

	private int[] monthsToExp = {36, 24, 12, 6, 0};
	private int months;

	public EmployeeSite() {
//		monthsToExp = new HashMap<Integer, Integer>();
//		monthsToExp.put(1, 36);
//		monthsToExp.put(2, 24);
//		monthsToExp.put(3, 12);
//		monthsToExp.put(4, 6);
	}

	@SuppressWarnings("null")
	@Transient
	public int getMonthsToExp() {
		if (orientationDate == null || orientationExpiration == null) {
			return 4;
		} else {
			Calendar c = Calendar.getInstance(), c1 = Calendar.getInstance();
			c.setTime(orientationDate);
			c1.setTime(orientationExpiration);
			int result = ((c1.get(Calendar.YEAR) - c.get(Calendar.YEAR)) * 12)+(c1.get(Calendar.MONTH)-c.get(Calendar.MONTH));
			if (result <= 6)
				return 3;
			else if (result <= 12)
				return 2;
			else if (result <= 24)
				return 1;
			else
				return 0;
		}
	}

	@Transient
	public void setMonthsToExp(int months) {
		this.months = months;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "employeeID", nullable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setOrientationExpiration(Date orientationExpiration) {
		this.orientationExpiration = orientationExpiration;
	}

	public void setOrientationExpiration() {
		Calendar c = Calendar.getInstance();
		c.setTime(orientationDate);
		c.add(Calendar.MONTH, monthsToExp[months]);
		this.orientationExpiration = c.getTime();
	}

	@Temporal(TemporalType.DATE)
	public Date getOrientationExpiration() {
		return orientationExpiration;
	}

	public void setOrientationDate(Date orientationDate) {	
		this.orientationDate = orientationDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getOrientationDate() {
		return orientationDate;
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

	@Transient
	public boolean canOperatorViewSite(int id) {
		if (id == operator.getId()) {
			return true;
		} 
		else if (operator.getParent() != null) {
			if (id == operator.getParent().getId()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("operator", operator.toJSON());
		json.put("jobSite", jobSite.toJSON());
		json.put("employee", employee.toJSON());
		json.put("orientationExpiration", orientationExpiration.getTime());
		json.put("orientationDate", orientationDate.getTime());

		return json;
	}
}
