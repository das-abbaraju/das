package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends PicsActionSupport {

	private int conID;
	private List<JobRole> roles;
	private List<OperatorCompetency> competencies;

	
	public JobCompetencyMatrix() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String execute() throws Exception {
		
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
