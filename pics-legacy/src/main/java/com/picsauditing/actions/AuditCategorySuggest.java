package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategory;

@SuppressWarnings("serial")
public class AuditCategorySuggest extends PicsActionSupport {
	private String q;
	private List<AuditCategory> results;
	private AuditCategoryDAO auditCategoryDAO;

	public AuditCategorySuggest(AuditCategoryDAO auditCategoryDAO) {
		this.auditCategoryDAO = auditCategoryDAO;
	}

	public String execute() throws Exception {
		results = new ArrayList<AuditCategory>();

		if (q == null || q.length() < 3)
			return SUCCESS;

		results = auditCategoryDAO.findCategoryNames(q);

		if (results == null)
			results = new ArrayList<AuditCategory>();

		return SUCCESS;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String search) {
		this.q = search;
	}

	public List<AuditCategory> getResults() {
		return results;
	}

	public void setResults(List<AuditCategory> results) {
		this.results = results;
	}

}
