package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.jpa.entities.JobRole;

@SuppressWarnings("serial")
public class DefineRoles extends ContractorActionSupport {
	protected JobRoleDAO jobRoleDao;

	protected String role;

	protected List<JobRole> jobRoles;

	public DefineRoles(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, JobRoleDAO jobRoleDao) {
		super(accountDao, auditDao);
		this.jobRoleDao = jobRoleDao;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		tryPermissions(OpPerms.DefineRoles);

		return SUCCESS;
	}

	public List<JobRole> getJobRoles() {
		if (jobRoles == null)
			jobRoles = jobRoleDao.findJobRolesByAccount(contractor.getId());
		return jobRoles;
	}

	public void setJobRoles(List<JobRole> jobRoles) {
		this.jobRoles = jobRoles;
	}

	public int getUsedCount(JobRole jobRole) {
		return jobRoleDao.getUsedCount(jobRole.getName());
	}
}
