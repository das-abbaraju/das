package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobCompetencyStats;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobRoles extends AccountActionSupport implements Preparable {

	protected JobRole role;
	protected OperatorCompetency competency;
	protected List<JobRole> jobRoles;
	protected List<JobCompetency> jobCompetencies = new ArrayList<JobCompetency>();
	protected List<OperatorCompetency> otherCompetencies = new ArrayList<OperatorCompetency>();
	private int competencyID = 0;
	private int auditID;

	protected JobRoleDAO jobRoleDAO;
	protected AccountDAO accountDAO;
	protected OperatorCompetencyDAO competencyDAO;
	protected EmployeeRoleDAO erDAO;

	public ManageJobRoles(AccountDAO accountDAO, JobRoleDAO jobRoleDAO, OperatorCompetencyDAO competencyDAO,
			EmployeeRoleDAO erDAO) {
		this.accountDAO = accountDAO;
		this.jobRoleDAO = jobRoleDAO;
		this.competencyDAO = competencyDAO;
		this.erDAO = erDAO;
	}

	@Override
	public void prepare() throws Exception {
		int roleID = getParameter("role.id");
		if (roleID > 0) {
			role = jobRoleDAO.find(roleID);
		}

		if (role != null) {
			account = role.getAccount();
		} else {
			int accountID = getParameter("id");
			if (accountID > 0)
				account = accountDAO.find(accountID);
		}

		if (role == null && account == null) {
			loadPermissions();
			account = accountDAO.find(permissions.getAccountId());
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.subHeading = account.getName();

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else {
			permissions.tryPermission(OpPerms.DefineRoles);

			if (permissions.getAccountId() != account.getId())
				permissions.tryPermission(OpPerms.AllOperators);
		}

		// Get auditID
		if (auditID > 0)
			ActionContext.getContext().getSession().put("auditID", auditID);
		else
			auditID = (ActionContext.getContext().getSession().get("auditID") == null ? 0 : (Integer) ActionContext
					.getContext().getSession().get("auditID"));

		if ("Description".equals(button)) {
			if (competencyID > 0)
				competency = competencyDAO.find(competencyID);

			return "description";
		}

		if ("Add".equals(button)) {
			role = new JobRole();

			return SUCCESS;
		}

		if ("Save".equals(button)) {
			if (role.getAccount() == null) {
				role.setAccount(account);
			}

			if (Strings.isEmpty(role.getName())) {
				addActionError("Name is required");
				return SUCCESS;
			}

			jobRoleDAO.save(role);
		}

		if ("Delete".equals(button)) {
			List<EmployeeRole> employeeRoles = erDAO.findWhere("e.jobRole.id = " + role.getId());

			if (employeeRoles.size() > 0) {
				role.setActive(false);
				jobRoleDAO.save(role);
			} else {
				jobRoleDAO.remove(role);
				role = null;
			}

			return redirect("ManageJobRoles.action?id=" + account.getId());
		}

		if (role != null) {
			jobCompetencies = jobRoleDAO.getCompetenciesByRole(role);
			List<OperatorCompetency> competencies = competencyDAO.findWhere("o.category != 'Other'");
			if (competencyID > 0) {
				if ("removeCompetency".equals(button)) {
					Iterator<JobCompetency> iterator = jobCompetencies.iterator();
					while (iterator.hasNext()) {
						JobCompetency current = iterator.next();
						if (current.getCompetency().getId() == competencyID) {
							iterator.remove();
							competencyDAO.remove(current);
						}
					}
				}
				if ("addCompetency".equals(button)) {
					for (OperatorCompetency operatorCompetency : competencies) {
						if (operatorCompetency.getId() == competencyID) {
							if (!roleContainsCompetency(operatorCompetency)) {
								JobCompetency jc = new JobCompetency();
								jc.setJobRole(role);
								jc.setCompetency(operatorCompetency);
								jc.setAuditColumns(permissions);
								competencyDAO.save(jc);
								jobCompetencies.add(jc);
							}
						}
					}
				}
				jobRoleDAO.save(role);
			}

			otherCompetencies.clear();

			Map<OperatorCompetency, JobCompetencyStats> jobCompetencyStats = competencyDAO.getJobCompetencyStats(role
					.getName());

			for (OperatorCompetency operatorCompetency : competencies) {
				if (!roleContainsCompetency(operatorCompetency)) {
					operatorCompetency.setJobCompentencyStats(jobCompetencyStats.get(operatorCompetency));
					otherCompetencies.add(operatorCompetency);
				}
			}
		}

		return SUCCESS;
	}

	private boolean roleContainsCompetency(OperatorCompetency operatorCompetency) {
		for (JobCompetency jc : jobCompetencies) {
			if (jc.getCompetency().equals(operatorCompetency))
				return true;
		}
		return false;
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

	public List<JobCompetency> getJobCompetencies() {
		return jobCompetencies;
	}

	public List<OperatorCompetency> getOtherCompetencies() {
		Collections.sort(otherCompetencies, new ByPercent());
		return otherCompetencies;
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

	private class ByPercent implements Comparator<OperatorCompetency> {
		@Override
		public int compare(OperatorCompetency o1, OperatorCompetency o2) {
			if (o1.getJobCompentencyStats() == null && o2.getJobCompentencyStats() == null)
				return 0;
			if (o1.getJobCompentencyStats() == null)
				return 1;
			if (o2.getJobCompentencyStats() == null)
				return -1;
			return -o1.getJobCompentencyStats().getPercent().compareTo(o2.getJobCompentencyStats().getPercent());
		}
	}
}
