package com.picsauditing.report;

import com.picsauditing.util.pagination.PaginationParameters;

public class ReportPaginationParameters extends PaginationParameters {

	private String query;
	private int userId;

	public ReportPaginationParameters(int userId, String query) {
		this.userId = userId;
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
