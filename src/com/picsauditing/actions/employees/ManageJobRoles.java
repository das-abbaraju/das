package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobCompetencyDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
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
public class ManageJobRoles extends AccountActionSupport {
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected EmployeeRoleDAO erDAO;
	@Autowired
	protected JobCompetencyDAO jobCompetencyDAO;
	@Autowired
	protected JobRoleDAO jobRoleDAO;
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	protected JobRole role;
	protected OperatorCompetency competency;
	protected List<JobRole> jobRoles;
	private int competencyID = 0;
	private int auditID;

	public ManageJobRoles() {
		subHeading = getText(getScope() + ".title");
	}

	@Override
	public String execute() throws Exception {
		startup();

		return SUCCESS;
	}

	public String get() throws Exception {
		startup();

		if (role == null)
			role = new JobRole();

		return "role";
	}

	public String save() throws Exception {
		startup();
		if (role.getAccount() == null) {
			role.setAccount(account);
		}

		if (Strings.isEmpty(role.getName())) {
			addActionError("Name is required");
			return SUCCESS;
		}

		jobRoleDAO.save(role);

		return SUCCESS;
	}

	public String delete() throws Exception {
		startup();
		List<EmployeeRole> employeeRoles = erDAO.findWhere("e.jobRole.id = " + role.getId());

		if (employeeRoles.size() > 0) {
			role.setActive(false);
			jobRoleDAO.save(role);
		} else {
			jobRoleDAO.remove(role);
		}

		return redirect("ManageJobRoles.action?id=" + account.getId());
	}

	public String addCompetency() throws Exception {
		startup();

		if (competencyID > 0) {
			for (JobCompetency jc : role.getJobCompetencies()) {
				if (jc.getCompetency().getId() == competencyID)
					addActionError(getText(getScope() + ".message.CompetencyExistsForRole"));
			}

			if (getActionErrors().size() == 0) {
				OperatorCompetency competency = operatorCompetencyDAO.find(competencyID);

				JobCompetency jc = new JobCompetency();
				jc.setJobRole(role);
				jc.setCompetency(competency);
				jc.setAuditColumns(permissions);
				jobCompetencyDAO.save(jc);

				role.getJobCompetencies().add(jc);
				jobRoleDAO.save(role);
			}
		} else {
			addActionError(getText(getScope() + ".message.MissingCompetency"));
		}

		return "competencies";
	}

	public String removeCompetency() throws Exception {
		startup();

		if (competencyID > 0) {
			Iterator<JobCompetency> iterator = role.getJobCompetencies().iterator();
			while (iterator.hasNext()) {
				JobCompetency jc = iterator.next();

				if (jc.getCompetency().getId() == competencyID) {
					iterator.remove();
					jobCompetencyDAO.remove(jc);
				}
			}
		} else {
			addActionError(getText(getScope() + ".message.MissingCompetency"));
		}

		return "competencies";
	}

	private void startup() throws Exception {
		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else {
			permissions.tryPermission(OpPerms.DefineRoles);

			if (permissions.isOperatorCorporate() && permissions.getAccountId() != account.getId())
				permissions.tryPermission(OpPerms.AllOperators);
		}

		if (role != null && role.getAccount() != null) {
			account = role.getAccount();
		} else {
			int accountID = getParameter("id");
			if (accountID > 0)
				account = accountDAO.find(accountID);
		}

		if (role == null && account == null && permissions.isContractor()) {
			account = accountDAO.find(permissions.getAccountId());
		}

		if (account == null && permissions.isContractor())
			throw new RecordNotFoundException("account");

		// Get auditID
		if (auditID > 0) {
			ActionContext.getContext().getSession().put("auditID", auditID);

			if (permissions.isAdmin()) {
				ContractorAudit audit = (ContractorAudit) accountDAO.find(ContractorAudit.class, auditID);
				this.account = audit.getContractorAccount();
			}
		} else {
			auditID = (ActionContext.getContext().getSession().get("auditID") == null ? 0 : (Integer) ActionContext
					.getContext().getSession().get("auditID"));
		}
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

	public List<JobRole> getJobRoles() {
		if (jobRoles == null)
			jobRoles = jobRoleDAO.findJobRolesByAccount(account.getId(), false);
		return jobRoles;
	}

	public void setJobRoles(List<JobRole> jobRoles) {
		this.jobRoles = jobRoles;
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
			Collections.sort(others, new Comparator<OperatorCompetency>() {
				@Override
				public int compare(OperatorCompetency o1, OperatorCompetency o2) {
					if (o1.getJobCompentencyStats() == null && o2.getJobCompentencyStats() == null)
						return 0;
					if (o1.getJobCompentencyStats() == null)
						return 1;
					if (o2.getJobCompentencyStats() == null)
						return -1;
					return -o1.getJobCompentencyStats().getPercent()
							.compareTo(o2.getJobCompentencyStats().getPercent());
				}
			});
			return others;
		}

		return null;
	}

	public void setCompetencyID(int competencyID) {
		this.competencyID = competencyID;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
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
