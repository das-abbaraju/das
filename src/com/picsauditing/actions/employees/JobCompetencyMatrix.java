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
		if (!forceLogin())
			return LOGIN;
		
		getPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		if (id == 0)
			throw new Exception("Missing id");

		account = accountDAO.find(id);
		roles = jobRoleDAO.findJobRolesByAccount(id, true);
		competencies = competencyDAO.findAll();
		map = jobRoleDAO.findJobCompetencies(id, true);

		this.subHeading = account.getName();

		return SUCCESS;
	}

	public List<JobRole> getRoles() {
		return roles;
	}
	
	public List<JobRole> getRoles(OperatorCompetency operatorCompetency) {
		// need to check if forward entries are all null to not include in list
		boolean usedRole = false;
		for(JobRole role : roles)
			if(map.get(role, operatorCompetency) != null)
				usedRole = true;

		return (usedRole)?roles:null;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public JobCompetency getJobCompetency(JobRole role, OperatorCompetency comp) {
		return map.get(role, comp);
	}

}
