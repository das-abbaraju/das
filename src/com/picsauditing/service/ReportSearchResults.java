package com.picsauditing.service;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

public class ReportSearchResults {

	private final List<BasicDynaBean> results;
	private final int totalResultSize;

	public ReportSearchResults(List<BasicDynaBean> results, int totalResultSize) {
		this.results = results;
		this.totalResultSize = totalResultSize;
	}

	// it would be very nice if we could return this as an unmodifiable list
	public List<BasicDynaBean> getResults() {
		return results;
	}

	public int getTotalResultSize() {
		return totalResultSize;
	}

}
