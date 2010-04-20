package com.picsauditing.actions.employees;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RoleSuggest extends PicsActionSupport {
	private String q;
	private List<String> roles;
	private Set<String> results;
	private JobRoleDAO jobRoleDAO;

	public RoleSuggest(JobRoleDAO jobRoleDAO) {
		this.jobRoleDAO = jobRoleDAO;

		roles = jobRoleDAO.findDistinctRolesOrderByPercent();
	}

	public String execute() throws Exception {
		results = new HashSet<String>();

		if (q == null || q.length() < 2)
			return SUCCESS;

		if (roles == null)
			roles = jobRoleDAO.findDistinctRolesOrderByPercent();

		for (String role : roles)
			for (String word : role.split(" "))
				if (word.toLowerCase().startsWith(q.toLowerCase())
						|| Strings.isSimilarTo(q, (word.length() >= q.length()) ? word.substring(0, q.length()) : ""))
					results.add(role);

		return SUCCESS;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String search) {
		this.q = search;
	}

	public Set<String> getResults() {
		return results;
	}

	public void setResults(Set<String> results) {
		this.results = results;
	}
}
