package com.picsauditing.util;

import java.util.Collections;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class ReportFilterEmployee extends ReportFilter {
	private int accountID;
	private Permissions permissions;

	private boolean showAccountName = true;
	private boolean showFirstName = true;
	private boolean showLastName = true;
	private boolean showEmail = true;
	private boolean showSsn = true;
	private boolean showLimitEmployees = false;
	private boolean showProjects = false;
	private boolean showAssessmentCenter = false;
	private boolean showJobRoles = false;
	private boolean showCompetencies = false;
	private boolean showOperators = false;

	private String accountName;
	private String firstName;
	private String lastName;
	private String email;
	private String ssn;
	private boolean limitEmployees = true;
	private int[] projects;
	private int[] assessmentCenters;
	private int[] jobRoles;
	private int[] competencies;
	private int[] operators;

	// Class variables
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}

	// Show booleans
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

	public boolean isShowJobRoles() {
		return showJobRoles;
	}

	public void setShowJobRoles(boolean showJobRoles) {
		this.showJobRoles = showJobRoles;
	}

	public boolean isShowCompetencies() {
		return showCompetencies;
	}

	public void setShowCompetencies(boolean showCompetencies) {
		this.showCompetencies = showCompetencies;
	}

	public boolean isShowOperators() {
		return showOperators;
	}

	public void setShowOperators(boolean showOperators) {
		this.showOperators = showOperators;
	}

	// Fields
	public String getAccountName() {
		if (Strings.isEmpty(accountName))
			accountName = ReportFilterAccount.getDefaultName();

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

	public int[] getJobRoles() {
		return jobRoles;
	}

	public void setJobRoles(int[] jobRoles) {
		this.jobRoles = jobRoles;
	}

	public int[] getCompetencies() {
		return competencies;
	}

	public void setCompetencies(int[] competencies) {
		this.competencies = competencies;
	}

	public int[] getOperators() {
		return operators;
	}

	public void setOperators(int[] operators) {
		this.operators = operators;
	}

	// Lists
	public List<JobSite> getProjectList() {
		JobSiteDAO jobSiteDAO = (JobSiteDAO) SpringUtils.getBean("JobSiteDAO");

		if (permissions.isOperatorCorporate())
			return jobSiteDAO.findByOperator(permissions.getAccountId(), true);
		else if (permissions.isContractor())
			return jobSiteDAO.findByContractor(permissions.getAccountId(), true);

		return null;
	}

	public List<Account> getAssessmentCenterList() {
		AccountDAO accountDAO = (AccountDAO) SpringUtils.getBean("AccountDAO");
		return accountDAO.findWhere("a.type = 'Assessment'");
	}

	public List<JobRole> getJobRoleList() {
		JobRoleDAO jobRoleDAO = (JobRoleDAO) SpringUtils.getBean("JobRoleDAO");
		return jobRoleDAO.findJobRolesByAccount(accountID, true);
	}

	public List<OperatorCompetency> getCompetencyList() {
		if (permissions != null) {
			OperatorCompetencyDAO operatorCompetencyDAO = (OperatorCompetencyDAO) SpringUtils
					.getBean("OperatorCompetencyDAO");

			if (permissions.isOperatorCorporate())
				return operatorCompetencyDAO.findByOperatorHierarchy(permissions.getVisibleAccounts());
			else if (permissions.isContractor())
				return operatorCompetencyDAO.findByContractor(permissions.getAccountId());
			else if (permissions.isAdmin()) {
				int auditID = (ActionContext.getContext().getSession().get("auditID") == null ? 0
						: (Integer) ActionContext.getContext().getSession().get("auditID"));

				if (auditID > 0) {
					ContractorAudit audit = (ContractorAudit) operatorCompetencyDAO
							.find(ContractorAudit.class, auditID);
					return operatorCompetencyDAO.findByContractor(audit.getContractorAccount().getId());
				}
			}
		}

		return Collections.emptyList();
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		if (permissions == null)
			return null;

		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "", permissions);
	}
}