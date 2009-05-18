package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.AmBest;

@SuppressWarnings("serial")
public class AmBestAction extends PicsActionSupport {
	private String search;
	private List<AmBest> results;

	public String execute() throws Exception {
		results = new ArrayList<AmBest>();
		
		results.add(newItem("1234", "ABC Corp"));
		results.add(newItem("8494", "Life Prudential"));
		results.add(newItem("8509", "ASDF Inc"));
		results.add(newItem("5503", "Juliet Mutual"));
		
		return SUCCESS;
	}

	private AmBest newItem(String naic, String company) {
		AmBest o = new AmBest();
		o.setNaic(naic);
		o.setCompanyName(company);
		return o;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public List<AmBest> getResults() {
		return results;
	}

	public void setResults(List<AmBest> results) {
		this.results = results;
	}

}
