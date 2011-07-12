package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobCompetencyDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobRoles extends PicsActionSupport {
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected EmployeeRoleDAO employeeRoleDAO;
	@Autowired
	protected JobCompetencyDAO jobCompetencyDAO;
	@Autowired
	protected JobRoleDAO jobRoleDAO;
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected Account account;
	protected JobRole role;
	protected OperatorCompetency competency;
	protected List<JobRole> jobRoles;
	private int auditID;

	@Before
	public void startup() throws Exception {
		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else {
			permissions.tryPermission(OpPerms.DefineRoles);

			if (permissions.isOperatorCorporate() && permissions.getAccountId() != account.getId())
				permissions.tryPermission(OpPerms.AllOperators);
		}

		if (role != null && role.getAccount() != null)
			account = role.getAccount();

		if (account == null && permissions.isContractor())
			account = accountDAO.find(permissions.getAccountId());

		// Get auditID
		if (auditID > 0) {
			ActionContext.getContext().getSession().put("auditID", auditID);

			if (permissions.isAdmin()) {
				ContractorAudit audit = contractorAuditDAO.find(auditID);
				account = audit.getContractorAccount();
			}
		} else {
			auditID = (ActionContext.getContext().getSession().get("auditID") == null ? 0 : (Integer) ActionContext
					.getContext().getSession().get("auditID"));
		}
	}

	public String get() throws Exception {
		if (role == null)
			role = new JobRole();

		return "role";
	}

	public String save() throws Exception {
		if (role.getAccount() == null)
			role.setAccount(account);

		if (Strings.isEmpty(role.getName())) {
			addActionError("Name is required");
			return SUCCESS;
		}

		jobRoleDAO.save(role);

		return SUCCESS;
	}

	public String delete() throws Exception {
		List<EmployeeRole> employeeRoles = employeeRoleDAO.findWhere("e.jobRole.id = " + role.getId());

		if (employeeRoles.size() > 0) {
			role.setActive(false);
			jobRoleDAO.save(role);
		} else {
			jobRoleDAO.remove(role);
		}

		return redirect("ManageJobRoles.action?id=" + account.getId());
	}

	public String addCompetency() throws Exception {
		if (competency != null) {
			for (JobCompetency jc : role.getJobCompetencies()) {
				if (competency.equals(jc.getCompetency()))
					addActionError(getText(String.format("%s.message.CompetencyExistsForRole", getScope())));
			}

			if (getActionErrors().size() == 0) {
				JobCompetency jc = new JobCompetency();
				jc.setJobRole(role);
				jc.setCompetency(competency);
				jc.setAuditColumns(permissions);
				jobCompetencyDAO.save(jc);

				role.getJobCompetencies().add(jc);
				jobRoleDAO.save(role);
			}
		} else {
			addActionError(getText(String.format("%s.message.MissingCompetency", getScope())));
		}

		return "competencies";
	}

	public String removeCompetency() throws Exception {
		if (competency != null) {
			Iterator<JobCompetency> iterator = role.getJobCompetencies().iterator();
			while (iterator.hasNext()) {
				JobCompetency jc = iterator.next();

				if (competency.equals(jc.getCompetency())) {
					iterator.remove();
					jobCompetencyDAO.remove(jc);
				}
			}
		} else {
			addActionError(getText(String.format("%s.message.MissingCompetency", getScope())));
		}

		return "competencies";
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public JobRole getRole() {
		return role;
	}

	public void setRole(JobRole role) {
		this.role = role;
	}

	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public List<JobRole> getJobRoles() {
		if (jobRoles == null)
			jobRoles = jobRoleDAO.findJobRolesByAccount(account.getId(), false);
		return jobRoles;
	}

	public int getUsedCount(JobRole jobRole) {
		return jobRoleDAO.getUsedCount(jobRole.getName());
	}

	public List<OperatorCompetency> getOtherCompetencies() {
		if (role != null) {
			List<OperatorCompetency> others = operatorCompetencyDAO.findAll();

			List<OperatorCompetency> exists = new ArrayList<OperatorCompetency>();
			for (JobCompetency jc : role.getJobCompetencies()) {
				exists.add(jc.getCompetency());
			}

			others.removeAll(exists);
			return others;
		}

		return null;
	}

	public List<OperatorAccount> getShellOps() {
		List<OperatorAccount> shellOps = new ArrayList<OperatorAccount>();

		if (account.isContractor()) {
			ContractorAccount con = (ContractorAccount) account;

			for (ContractorOperator co : con.getOperators()) {
				if (co.getOperatorAccount().isRequiresCompetencyReview())
					shellOps.add(co.getOperatorAccount());
			}
		}

		return shellOps;
	}
}
