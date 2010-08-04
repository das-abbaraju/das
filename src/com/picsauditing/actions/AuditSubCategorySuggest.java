package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.jpa.entities.AuditSubCategory;

@SuppressWarnings("serial")
public class AuditSubCategorySuggest extends PicsActionSupport {
	private String q;
	private List<AuditSubCategory> results;
	private AuditSubCategoryDAO auditSubCategoryDAO;

	public AuditSubCategorySuggest(AuditSubCategoryDAO auditSubCategoryDAO) {
		this.auditSubCategoryDAO = auditSubCategoryDAO;
	}

	public String execute() throws Exception {
		results = new ArrayList<AuditSubCategory>();

		if (q == null || q.length() < 3)
			return SUCCESS;

		results = auditSubCategoryDAO.findSubCategoryNames(q);

		if (results == null)
			results = new ArrayList<AuditSubCategory>();

		return SUCCESS;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String search) {
		this.q = search;
	}

	public List<AuditSubCategory> getResults() {
		return results;
	}

	public void setResults(List<AuditSubCategory> results) {
		this.results = results;
	}

}
