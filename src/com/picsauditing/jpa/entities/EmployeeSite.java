package com.picsauditing.jpa.entities;

import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee_site")
public class EmployeeSite extends BaseHistory implements JSONable {

	private OperatorAccount operator;
	private JobSite jobSite;
	private Employee employee;

	private Date orientationDate;
	private Date orientationExpiration;

	private int[] monthsToExp = {36, 24, 12, 6, 0};
	private int months;

	@Transient
	public int getMonthsToExp() {
		if (orientationDate == null || orientationExpiration == null) {
			return 4;
		} else {
			Calendar c = Calendar.getInstance(), c1 = Calendar.getInstance();
			c.setTime(orientationDate);
			c1.setTime(orientationExpiration);
			int result = ((c1.get(Calendar.YEAR) - c.get(Calendar.YEAR)) * 12)
					+ (c1.get(Calendar.MONTH) - c.get(Calendar.MONTH));
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
		} else if (operator.getParent() != null) {
			if (id == operator.getParent().getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void defaultDates() {
		Date now = new Date();
		if (jobSite == null || jobSite.getProjectStop() == null || now.after(jobSite.getProjectStop())) {
			super.defaultDates();
		} else {
			effectiveDate = new Date();
			expirationDate = jobSite.getProjectStop();
		}
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

	@Transient
	public boolean isActive() {
		return isCurrent() && (jobSite == null || jobSite.isActive(new Date()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		EmployeeSite that = (EmployeeSite) o;

		if (months != that.months) return false;
		if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
		if (jobSite != null ? !jobSite.equals(that.jobSite) : that.jobSite != null) return false;
		if (!Arrays.equals(monthsToExp, that.monthsToExp)) return false;
		if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
		if (orientationDate != null ? !orientationDate.equals(that.orientationDate) : that.orientationDate != null)
			return false;
		if (orientationExpiration != null ? !orientationExpiration.equals(that.orientationExpiration) : that.orientationExpiration != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (operator != null ? operator.hashCode() : 0);
		result = 31 * result + (jobSite != null ? jobSite.hashCode() : 0);
		result = 31 * result + (employee != null ? employee.hashCode() : 0);
		result = 31 * result + (orientationDate != null ? orientationDate.hashCode() : 0);
		result = 31 * result + (orientationExpiration != null ? orientationExpiration.hashCode() : 0);
		result = 31 * result + (monthsToExp != null ? Arrays.hashCode(monthsToExp) : 0);
		result = 31 * result + months;
		return result;
	}
}
