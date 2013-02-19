package com.picsauditing.actions.contractors;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.ContractorAppIndexSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class RequestNewContractorSearch extends PicsActionSupport {
	private String term;
	private String type;
	private List<String> usedTerms = new ArrayList<String>();
	private Set<ContractorAppIndexSearch.SearchResult> results;

	public String search() throws Exception {
		ContractorAppIndexSearch contractorAppIndexSearch = new ContractorAppIndexSearch(permissions);
		usedTerms = contractorAppIndexSearch.getTermsWithResults();

		results = contractorAppIndexSearch.searchOn(term, type);
		if (results == null || results.isEmpty()) {
			output = "No matches";
			return BLANK;
		}

		return SUCCESS;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getUsedTerms() {
		return usedTerms;
	}

	public Set<ContractorAppIndexSearch.SearchResult> getResults() {
		return results;
	}
}