package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageEmployeeSite extends ManageEmployees {
	@Autowired
	protected EmployeeSiteDAO employeeSiteDAO;

	private OperatorAccount operator;
	private EmployeeSite employeeSite;
	private JobSite jobSite = new JobSite();

	public String add() {
		if (employee != null && (operator != null || jobSite.getId() > 0)) {
			EmployeeSite employeeSite = new EmployeeSite();
			employeeSite.setEmployee(employee);

			if (operator != null && jobSite.getId() == 0) {
				employeeSite.setOperator(operator);
			} else {
				employeeSite.setJobSite(jobSite);
				employeeSite.setOperator(jobSite.getOperator());
			}

			employeeSite.setAuditColumns(permissions);
			employeeSite.defaultDates();

			employeeSiteDAO.save(employeeSite);
			employee.getEmployeeSites().add(employeeSite);

			String note = "Added ";
			if (jobSite.getId() > 0) {
				note += "OQ Project " + jobSite.getOperator().getName() + ": " + jobSite.getLabel();
				addNote(note, jobSite.getOperator().getId());
			} else if (operator.isRequiresCompetencyReview()) {
				note += "HSE site " + operator.getName();
				addNote(note, operator.getId());
			} else {
				note += "Client site " + operator.getName();
				addNote(note, operator.getId());
			}
		}

		return SUCCESS;
	}

	public String addNew() {
		boolean jobSiteValid = !Strings.isEmpty(jobSite.getLabel()) && !Strings.isEmpty(jobSite.getName());

		if (employee != null && operator != null && jobSiteValid) {
			jobSite.setAuditColumns(permissions);
			jobSite.setOperator(operator);
			jobSite = jobSiteDAO.save(jobSite);

			employeeSite = new EmployeeSite();
			employeeSite.setAuditColumns(permissions);
			employeeSite.setEmployee(employee);
			employeeSite.setJobSite(jobSite);
			employeeSite.setOperator(operator);
			employeeSite.defaultDates();
			employeeSiteDAO.save(employeeSite);

			return SUCCESS;
		}

		return "new";
	}

	public String edit() {
		return "edit";
	}

	public String expire() {
		if (employee != null && operator != null) {
			for (EmployeeSite existingEmployeeSite : employee.getEmployeeSites()) {
				if (existingEmployeeSite.getOperator().equals(operator)) {
					employeeSite = existingEmployeeSite;
				}
			}
		}

		if (employeeSite != null) {
			if (employee == null) {
				employee = employeeSite.getEmployee();
				account = employee.getAccount();
			}

			employeeSite.expire();
			employeeSite.setAuditColumns(permissions);

			employeeSiteDAO.save(employeeSite);

			boolean hasProject = employeeSite.getJobSite() != null;

			String type = "Site";
			if (hasProject) {
				type = "Project";
			}

			String projectSite = employeeSite.getOperator().getName();
			if (hasProject) {
				projectSite += ": " + employeeSite.getJobSite().getLabel();
			}

			addNote(String.format("Expired %s %s", type, projectSite));
		}

		return SUCCESS;
	}

	public String save() {
		if (employeeSite != null) {
			List<String> notes = new ArrayList<String>();
			EmployeeSite existingSite = employeeSiteDAO.find(employeeSite.getId());

			if (employee == null) {
				employee = employeeSite.getEmployee();
				account = employee.getAccount();
			}

			if (employeeSite.getEffectiveDate() != null
					&& !employeeSite.getEffectiveDate().equals(existingSite.getEffectiveDate())) {
				notes.add("Updated start date to " + employeeSite.getEffectiveDate());
			} else if (employeeSite.getEffectiveDate() == null && existingSite.getEffectiveDate() != null) {
				notes.add("Removed start date");
			}

			if (employeeSite.getExpirationDate() != null
					&& !employeeSite.getExpirationDate().equals(existingSite.getExpirationDate())) {
				notes.add("Updated stop date to " + employeeSite.getExpirationDate());
			} else if (employeeSite.getExpirationDate() == null && existingSite.getExpirationDate() != null) {
				notes.add("Removed stop date");
			}

			if (employeeSite.getOrientationDate() != null
					&& !employeeSite.getOrientationDate().equals(existingSite.getOrientationDate())) {
				notes.add("Updated orientation date to " + employeeSite.getOrientationDate());

				if (employeeSite.getOrientationExpiration() != null
						&& !employeeSite.getOrientationExpiration().equals(existingSite.getOrientationExpiration())) {
					notes.add("Updated orientation expiration date to " + employeeSite.getOrientationExpiration());
				}
			} else if (employeeSite.getOrientationDate() == null && existingSite.getOrientationDate() != null) {
				notes.add("Removed orientation date");
				employeeSite.setOrientationExpiration(null);
			}

			existingSite.setEffectiveDate(employeeSite.getEffectiveDate());
			existingSite.setExpirationDate(employeeSite.getExpirationDate());
			existingSite.setOrientationDate(employeeSite.getOrientationDate());
			existingSite.setOrientationExpiration(employeeSite.getOrientationExpiration());
			existingSite.setAuditColumns(permissions);

			employeeSiteDAO.save(existingSite);
			addNote(Strings.implode(notes));
		}

		return SUCCESS;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public EmployeeSite getEmployeeSite() {
		return employeeSite;
	}

	public void setEmployeeSite(EmployeeSite employeeSite) {
		this.employeeSite = employeeSite;
	}

	public JobSite getJobSite() {
		return jobSite;
	}

	public void setJobSite(JobSite jobSite) {
		this.jobSite = jobSite;
	}
}
