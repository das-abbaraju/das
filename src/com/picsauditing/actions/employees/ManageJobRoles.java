package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
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
	protected EmployeeRoleDAO employeeRoleDAO;
	@Autowired
	protected JobCompetencyDAO jobCompetencyDAO;
	@Autowired
	protected JobRoleDAO jobRoleDAO;
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	private ContractorAudit audit;
	protected JobRole role;
	protected OperatorCompetency competency;
	protected List<JobRole> jobRoles;
	protected int contractorId = 0;

	@Before
	public void startup() throws Exception {
		findAccount();
	}

	public String execute() throws Exception {
		checkPermissions();

		findAccount();

		return SUCCESS;
	}

	private void findAccount() throws Exception {
		if (audit != null) {
			// Default in case role is not specified
			account = audit.getContractorAccount();
		}

		if (role != null && role.getAccount() != null) {
			account = role.getAccount();
		}

		if (account == null && permissions.isContractor()) {
			account = accountDAO.find(permissions.getAccountId());
			contractorId = permissions.getAccountId();
		}

		if (account == null) {
			contractorId = id;
			account = accountDAO.find(id);
		}

		if (account == null) {
			throw new RecordNotFoundException("account");
		}

		assert (account.isContractor());
	}

	public String get() throws Exception {
		checkPermissions();

		if (role == null)
			role = new JobRole();

		return "role";
	}

	public String save() throws Exception {
		checkPermissions();

		if (role.getAccount() == null && account != null) {
			role.setAccount(account);
		}

		if (Strings.isEmpty(role.getName())) {
			addActionError("Name is required");
			return SUCCESS;
		}

		jobRoleDAO.save(role);

		return setUrlForRedirect("ManageJobRoles.action?" + getUrlOptions());
	}

	public String delete() throws Exception {
		checkPermissions();

		List<EmployeeRole> employeeRoles = employeeRoleDAO.findWhere("e.jobRole.id = " + role.getId());

		if (employeeRoles.size() > 0) {
			role.setActive(false);
			jobRoleDAO.save(role);
		} else {
			jobRoleDAO.remove(role);
		}

		return setUrlForRedirect("ManageJobRoles.action?" + getUrlOptions());
	}

	private String getUrlOptions() {
		String urlOptions = "";

		if (audit == null) {
			urlOptions = "account=" + account.getId();
		} else {
			urlOptions = "audit=" + audit.getId();

			if (questionId > 0) {
				urlOptions += "&questionId=" + questionId;
			}
		}

		return urlOptions;
	}

	public String addCompetency() throws Exception {
		checkPermissions();

		if (competency != null) {
			for (JobCompetency jc : role.getJobCompetencies()) {
				if (competency.equals(jc.getCompetency()))
					addActionError(getText("ManageJobRoles.message.CompetencyExistsForRole"));
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
			addActionError(getText("ManageJobRoles.message.MissingCompetency"));
		}

		return "competencies";
	}

	public String removeCompetency() throws Exception {
		checkPermissions();

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
			addActionError(getText("ManageJobRoles.message.MissingCompetency"));
		}

		return "competencies";
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public List<JobRole> getJobRoles() {
		if (jobRoles == null) {
			jobRoles = jobRoleDAO.findJobRolesByAccount(account.getId(), false);
		}

		return jobRoles;
	}

	public int getUsedCount(JobRole jobRole) {
		return jobRoleDAO.getUsedCount(jobRole.getName());
	}

	private void checkPermissions() throws NoRightsException {
		if (permissions.isContractor()) {
			if (!permissions.hasPermission(OpPerms.ContractorAdmin)
					&& !permissions.hasPermission(OpPerms.ContractorSafety)) {
				throw new NoRightsException("Contractor Admin or Safety");
			}
		} else if (permissions.isOperatorCorporate()) {
			permissions.tryPermission(OpPerms.DefineRoles);

			if (permissions.getAccountId() != account.getId()) {
				permissions.tryPermission(OpPerms.AllOperators);
			}
		}
	}

	// "other" means "unassigned"/"available"
	public List<OperatorCompetency> getOtherCompetencies() throws Exception {
		findAccount();

		if (role != null) {
			List<OperatorCompetency> others;
			ContractorAccount contractor = (ContractorAccount) account;
			others = operatorCompetencyDAO.findByOperatorHierarchy(operatorIDs(contractor));

			List<OperatorCompetency> alreadyAssigned = new ArrayList<OperatorCompetency>();
			for (JobCompetency jc : role.getJobCompetencies()) {
				alreadyAssigned.add(jc.getCompetency());
			}

			others.removeAll(alreadyAssigned);
			return others;
		}

		return null;
	}

	private Set<Integer> operatorIDs(ContractorAccount contractor) {
		Set<Integer> opIds = new HashSet<Integer>();

		for (ContractorOperator op : contractor.getOperators()) {
			opIds.add(op.getOperatorAccount().getId());
		}

		return opIds;
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
