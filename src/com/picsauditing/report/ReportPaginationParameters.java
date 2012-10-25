package com.picsauditing.report;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.pagination.PaginationParameters;

public class ReportPaginationParameters extends PaginationParameters {

	private String query;
	private Permissions permissions;

	public ReportPaginationParameters(Permissions permissions, String query) {
		this.permissions = permissions;
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
}