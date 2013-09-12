package com.picsauditing.actions.employees;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class CategorySuggest extends PicsActionSupport {
	private String q;
	private List<String> categories;
	private Set<String> results;
	private OperatorCompetencyDAO operatorCompetencyDAO;

	public CategorySuggest(OperatorCompetencyDAO operatorCompetencyDAO) {
		this.operatorCompetencyDAO = operatorCompetencyDAO;

		categories = operatorCompetencyDAO.findDistinctCategories();
	}

	public String execute() throws Exception {
		results = new HashSet<String>();

		if (q == null || q.length() < 2)
			return SUCCESS;

		if (categories == null)
			operatorCompetencyDAO.findDistinctCategories();

		for (String category : categories)
			for (String word : category.split(" "))
				if (word.toLowerCase().startsWith(q.toLowerCase())
						|| Strings.isSimilarTo(q, (word.length() >= q.length()) ? word.substring(0, q.length()) : ""))
					results.add(category);

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
