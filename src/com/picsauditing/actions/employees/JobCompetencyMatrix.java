package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends PicsActionSupport {

	private int conID;
	private List<JobRole> roles;
	private List<OperatorCompetency> competencies;
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
		
		roles = jobRoleDAO.findContractorJobRoles(conID);
		competencies = competencyDAO.findAll();

		return SUCCESS;
	}

	public List<JobRole> getRoles() {
		return roles;
	}

	public void setRoles(List<JobRole> roles) {
		this.roles = roles;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public void setCompetencies(List<OperatorCompetency> competencies) {
		this.competencies = competencies;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

}
