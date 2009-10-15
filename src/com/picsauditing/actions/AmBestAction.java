package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.jpa.entities.AmBest;

@SuppressWarnings("serial")
public class AmBestAction extends PicsActionSupport {
	private String q;
	private List<AmBest> results;
	private AmBestDAO amBestDao;

	public AmBestAction(AmBestDAO amBestDao) {
		this.amBestDao = amBestDao;
	}

	public String execute() throws Exception {
		results = new ArrayList<AmBest>();

		if (q == null || q.length() < 3)
			return SUCCESS;

		results = amBestDao.findByCompanyName(q);

		if (results == null)
			results = new ArrayList<AmBest>();

		if (results.size() == 0)
			results.add(newItem("UNKNOWN", q));

		return SUCCESS;
	}

	private AmBest newItem(String naic, String company) {
		AmBest o = new AmBest();
		o.setNaic(naic);
		o.setCompanyName(company);
		return o;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String search) {
		this.q = search;
	}

	public List<AmBest> getResults() {
		return results;
	}

	public void setResults(List<AmBest> results) {
		this.results = results;
	}

}
