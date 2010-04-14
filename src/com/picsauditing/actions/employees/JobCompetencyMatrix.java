package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends PicsActionSupport {

	private int conID;
	private List<JobRole> roles;
	private List<OperatorCompetency> competencies;
	private DoubleMap<JobRole, OperatorCompetency, JobCompetency> map;
	private JobRoleDAO jobRoleDAO;
	private OperatorCompetencyDAO competencyDAO;

	public JobCompetencyMatrix(JobRoleDAO jobRoleDAO, OperatorCompetencyDAO competencyDAO) {
		this.jobRoleDAO = jobRoleDAO;
		this.competencyDAO = competencyDAO;
	}

	@Override
	public String execute() throws Exception {
		getPermissions();
		if (permissions.isContractor())
			conID = permissions.getAccountId();

		if (conID == 0)
			throw new Exception("Missing conID");

		roles = jobRoleDAO.findJobRolesByAccount(conID);
		competencies = competencyDAO.findAll();
		map = jobRoleDAO.findJobCompetencies(conID);

		return SUCCESS;
	}

	public List<JobRole> getRoles() {
		return roles;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public JobCompetency getJobCompetency(JobRole role, OperatorCompetency comp) {
		return map.get(role, comp);
	}

}
