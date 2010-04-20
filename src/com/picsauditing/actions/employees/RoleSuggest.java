package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.JobRoleDAO;

@SuppressWarnings("serial")
public class RoleSuggest extends PicsActionSupport {
	private String q;
	private List<String> results;
	private JobRoleDAO jobRoleDAO;

	public RoleSuggest(JobRoleDAO jobRoleDAO) {
		this.jobRoleDAO = jobRoleDAO;
	}

	public String execute() throws Exception {
		results = new ArrayList<String>();

		if (q == null || q.length() < 2)
			return SUCCESS;

		results = jobRoleDAO.findDistinctRolesOrderByCount(q);

		return SUCCESS;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String search) {
		this.q = search;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}
}
