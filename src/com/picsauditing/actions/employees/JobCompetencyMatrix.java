package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends AccountActionSupport {

	private List<JobRole> roles;
	private List<OperatorCompetency> competencies;
	private DoubleMap<JobRole, OperatorCompetency, JobCompetency> map;

	protected JobRoleDAO jobRoleDAO;
	protected AccountDAO accountDAO;
	private OperatorCompetencyDAO competencyDAO;

	public JobCompetencyMatrix(AccountDAO accountDAO, JobRoleDAO jobRoleDAO, OperatorCompetencyDAO competencyDAO) {
		this.accountDAO = accountDAO;
		this.jobRoleDAO = jobRoleDAO;
		this.competencyDAO = competencyDAO;
	}

	@Override
	public String execute() throws Exception {
		getPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		if (id == 0)
			throw new Exception("Missing id");

		account = accountDAO.find(id);
		roles = jobRoleDAO.findJobRolesByAccount(id);
		competencies = competencyDAO.findAll();
		map = jobRoleDAO.findJobCompetencies(id);

		this.subHeading = account.getName();

		return SUCCESS;
	}

	public List<JobRole> getRoles() {
		return roles;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public JobCompetency getJobCompetency(JobRole role, OperatorCompetency comp) {
		return map.get(role, comp);
	}

}
