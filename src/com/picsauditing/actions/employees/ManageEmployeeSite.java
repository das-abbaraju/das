package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageEmployeeSite extends ManageEmployees {
	@Autowired
	protected EmployeeSiteDAO employeeSiteDAO;
	@Autowired
	protected JobSiteDAO jobSiteDAO;

	private OperatorAccount operator;
	private EmployeeSite employeeSite = new EmployeeSite();
	private JobSite jobSite = new JobSite();

	public String add() {
		if (employee != null && operator != null) {
			EmployeeSite employeeSite = new EmployeeSite();
			employeeSite.setEmployee(employee);

			if (operator.getId() > 0) {
				employeeSite.setOperator(operator);
			} else {
				employeeSite.setJobSite(jobSiteDAO.find(-1 * operator.getId()));
				employeeSite.setOperator(employeeSite.getJobSite().getOperator());
			}

			employeeSite.setAuditColumns(permissions);
			employeeSite.defaultDates();

			employeeSiteDAO.save(employeeSite);
			employee.getEmployeeSites().add(employeeSite);

			addNote("Added "
					+ (employeeSite.getJobSite() != null ? "OQ project " + employeeSite.getOperator().getName() + ": "
							+ employeeSite.getJobSite().getLabel() : "HSE site " + employeeSite.getOperator().getName()));
		}

		return SUCCESS;
	}

	public String addNew() {
		if (!Strings.isEmpty(jobSite.getLabel()) && !Strings.isEmpty(jobSite.getName())) {
			jobSite.setAuditColumns(permissions);
			jobSite.setOperator(operator);
			jobSite = jobSiteDAO.save(jobSite);

			employeeSite.setAuditColumns(permissions);
			employeeSite.setEmployee(employee);
			employeeSite.setJobSite(jobSite);
			employeeSite.setOperator(operator);
			employeeSite.defaultDates();
			employeeSiteDAO.save(employeeSite);
		}

		return SUCCESS;
	}

	public String edit() {
		if (employee != null && childID != 0) {
			List<String> notes = new ArrayList<String>();

			EmployeeSite es = employeeSiteDAO.find(childID);

			if (employeeSite.getEffectiveDate() != null
					&& !employeeSite.getEffectiveDate().equals(es.getEffectiveDate()))
				notes.add("Updated start date to " + employeeSite.getEffectiveDate());
			else if (employeeSite.getEffectiveDate() == null && es.getEffectiveDate() != null)
				notes.add("Removed start date");

			if (employeeSite.getExpirationDate() != null
					&& !employeeSite.getExpirationDate().equals(es.getExpirationDate()))
				notes.add("Updated stop date to " + employeeSite.getExpirationDate());
			else if (employeeSite.getExpirationDate() == null && es.getExpirationDate() != null)
				notes.add("Removed stop date");

			if (employeeSite.getOrientationDate() != null
					&& !employeeSite.getOrientationDate().equals(es.getOrientationDate())) {
				notes.add("Updated orientation date to " + employeeSite.getOrientationDate());

				if (employeeSite.getOrientationExpiration() != null
						&& !employeeSite.getOrientationExpiration().equals(es.getOrientationExpiration()))
					notes.add("Updated orientation expiration date to " + employeeSite.getOrientationExpiration());
			} else if (employeeSite.getOrientationDate() == null && es.getOrientationDate() != null) {
				notes.add("Removed orientation date");
				employeeSite.setOrientationExpiration(null);
			}

			es.setEffectiveDate(employeeSite.getEffectiveDate());
			es.setExpirationDate(employeeSite.getExpirationDate());
			es.setOrientationDate(employeeSite.getOrientationDate());
			es.setOrientationExpiration(employeeSite.getOrientationExpiration());
			es.setAuditColumns(permissions);

			employeeSiteDAO.save(es);
			addNote(Strings.implode(notes));
		}

		return "edit";
	}
}
