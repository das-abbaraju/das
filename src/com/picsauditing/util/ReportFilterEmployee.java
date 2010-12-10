package com.picsauditing.util;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.JobSite;

@SuppressWarnings("serial")
public class ReportFilterEmployee extends ReportFilter {
	private Permissions permissions;

	protected boolean showAccountName = true;
	protected boolean showFirstName = true;
	protected boolean showLastName = true;
	protected boolean showEmail = true;
	protected boolean showSsn = true;
	protected boolean showLimitEmployees = false;
	protected boolean showProjects = false;
	protected boolean showAssessmentCenter = false;

	protected String accountName;
	protected String firstName;
	protected String lastName;
	protected String email;
	protected String ssn;
	protected boolean limitEmployees = true;
	protected int[] projects;
	protected int[] assessmentCenters;
	
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	public boolean isShowAccountName() {
		return showAccountName;
	}

	public void setShowAccountName(boolean showAccountName) {
		this.showAccountName = showAccountName;
	}

	public boolean isShowFirstName() {
		return showFirstName;
	}

	public void setShowFirstName(boolean showFirstName) {
		this.showFirstName = showFirstName;
	}

	public boolean isShowLastName() {
		return showLastName;
	}

	public void setShowLastName(boolean showLastName) {
		this.showLastName = showLastName;
	}

	public boolean isShowEmail() {
		return showEmail;
	}

	public void setShowEmail(boolean showEmail) {
		this.showEmail = showEmail;
	}

	public boolean isShowSsn() {
		return showSsn;
	}

	public void setShowSsn(boolean showSsn) {
		this.showSsn = showSsn;
	}
	
	public boolean isShowLimitEmployees() {
		return showLimitEmployees;
	}
	
	public void setShowLimitEmployees(boolean showLimitEmployees) {
		this.showLimitEmployees = showLimitEmployees;
	}
	
	public boolean isShowProjects() {
		return showProjects;
	}
	
	public void setShowProjects(boolean showProjects) {
		this.showProjects = showProjects;
	}
	
	public boolean isShowAssessmentCenter() {
		return showAssessmentCenter;
	}
	
	public void setShowAssessmentCenter(boolean showAssessmentCenter) {
		this.showAssessmentCenter = showAssessmentCenter;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String employeeFN) {
		this.firstName = employeeFN;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String employeeLN) {
		this.lastName = employeeLN;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		ssn = ssn.replaceAll("[^X0-9]", "");
		if (ssn.length() <= 9)
			this.ssn = ssn;
	}
	
	public boolean isLimitEmployees() {
		return limitEmployees;
	}
	
	public void setLimitEmployees(boolean limitEmployees) {
		this.limitEmployees = limitEmployees;
	}
	
	public int[] getProjects() {
		return projects;
	}
	
	public void setProjects(int[] projects) {
		this.projects = projects;
	}
	
	public int[] getAssessmentCenters() {
		return assessmentCenters;
	}
	
	public void setAssessmentCenters(int[] assessmentCenters) {
		this.assessmentCenters = assessmentCenters;
	}
	
	// Lists
	public List<JobSite> getProjectList() {
		JobSiteDAO siteDAO = (JobSiteDAO) SpringUtils.getBean("JobSiteDAO");
		
		if (permissions.isOperatorCorporate())
			return siteDAO.findByOperator(permissions.getAccountId());
		else if (permissions.isContractor())
			return siteDAO.findByContractor(permissions.getAccountId());
		
		return null;
	}
	
	public List<Account> getAssessmentCenterList() {
		AccountDAO accountDAO = (AccountDAO) SpringUtils.getBean("AccountDAO");
		return accountDAO.findWhere("a.type = 'Assessment'");
	}
}
