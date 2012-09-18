package com.picsauditing.report;

import com.picsauditing.util.pagination.PaginationParameters;

public class ReportPaginationParameters extends PaginationParameters {

	private String query;
	private int userId;
	private int accountId;

	public ReportPaginationParameters(int userId, int accountId, String query) {
		this.userId = userId;
		this.accountId = accountId;
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

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
}
