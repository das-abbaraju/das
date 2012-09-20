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
	protected JobRole role = new JobRole();
	protected int contractorId = 0;

	protected List<JobRole> jobRoles;
	protected List<OperatorCompetency> competenciesToAdd = new ArrayList<OperatorCompetency>();

	@Before
	public void startup() throws Exception {
		findAccount();
	}

	public String execute() throws Exception {
		checkPermissions();

		findAccount();

		return SUCCESS;
	}

	public String get() throws Exception {
		checkPermissions();

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

		saveCompetencies();

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

	public JobRole getRole() {
		return role;
	}

	public void setRole(JobRole role) {
		this.role = role;
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

	public List<OperatorCompetency> getCompetenciesToAdd() {
		return competenciesToAdd;
	}

	public void setCompetenciesToAdd(List<OperatorCompetency> competenciesToAdd) {
		this.competenciesToAdd = competenciesToAdd;
	}

	public int getUsedCount(JobRole jobRole) {
		return jobRoleDAO.getUsedCount(jobRole.getName());
	}

	// "other" means "unassigned"/"available"
	public List<OperatorCompetency> getOtherCompetencies() throws Exception {
		findAccount();

		List<OperatorCompetency> others = getOperatorCompetencies();

		for (JobCompetency jc : role.getJobCompetencies()) {
			others.remove(jc.getCompetency());
		}

		others.removeAll(competenciesToAdd);

		return others;
	}

	public List<OperatorCompetency> getOperatorCompetencies() {
		ContractorAccount contractor = (ContractorAccount) account;
		return operatorCompetencyDAO.findByOperatorHierarchy(operatorIDs(contractor));
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

	public boolean isPreviouslySelected(OperatorCompetency competency) {
		if (role.getId() > 0) {
			for (JobCompetency jobCompetency : role.getJobCompetencies()) {
				if (jobCompetency.getCompetency().equals(competency)) {
					return true;
				}
			}
		}

		return false;
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

	private Set<Integer> operatorIDs(ContractorAccount contractor) {
		Set<Integer> opIds = new HashSet<Integer>();

		for (ContractorOperator op : contractor.getOperators()) {
			opIds.add(op.getOperatorAccount().getId());
		}

		return opIds;
	}

	private void saveCompetencies() {
		removeDeselectedCompetenciesFromRole();
		removeExistingCompetenciesFromSelected();
		saveNewlySelectedCompetencies();
	}

	private void removeDeselectedCompetenciesFromRole() {
		Iterator<JobCompetency> jobCompetencyIterator = role.getJobCompetencies().iterator();
		while (jobCompetencyIterator.hasNext()) {
			JobCompetency jobCompetency = jobCompetencyIterator.next();

			if (!competenciesToAdd.contains(jobCompetency.getCompetency())) {
				jobCompetencyIterator.remove();
				jobCompetencyDAO.remove(jobCompetency);
			}
		}
	}

	private void removeExistingCompetenciesFromSelected() {
		for (JobCompetency jobCompetency : role.getJobCompetencies()) {
			competenciesToAdd.remove(jobCompetency.getCompetency());
		}
	}

	private void saveNewlySelectedCompetencies() {
		for (OperatorCompetency remaining : competenciesToAdd) {
			JobCompetency jobCompetency = new JobCompetency();
			jobCompetency.setJobRole(role);
			jobCompetency.setCompetency(remaining);
			jobCompetency.setAuditColumns(permissions);
			jobCompetencyDAO.save(jobCompetency);

			role.getJobCompetencies().add(jobCompetency);
		}
	}
}
