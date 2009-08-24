package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.jpa.entities.AmBest;

@SuppressWarnings("serial")
public class AmBestAction extends PicsActionSupport {
	private String search;
	private List<AmBest> results;
	private AmBestDAO amBestDao;

	public AmBestAction(AmBestDAO amBestDao) {
		this.amBestDao = amBestDao;
	}

	public String execute() throws Exception {
		results = new ArrayList<AmBest>();
		
		if (search == null || search.length() < 3)
			return SUCCESS;
		
		results = amBestDao.findByCompanyName(search);

		if (results == null)
			results = new ArrayList<AmBest>();
		
		if (results.size() == 0)
			results.add(newItem("UNKNOWN", search));
		
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
