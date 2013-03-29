package com.picsauditing.service;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;

public final class ReportSearch {

	private final Permissions permissions;
	private final String sortType;
	private final String sortDirection;

	public ReportSearch(Permissions permissions, String sortType, String sortDirection) {
		this.permissions = permissions;
		this.sortType = sortType;
		this.sortDirection = sortDirection;
	}

	public final Permissions getPermissions() {
		return permissions;
	}

	public final String getSortType() {
		if (sortType == null) {
			return ManageReports.ALPHA_SORT;
		}

		return sortType;
	}

	public final String getSortDirection() {
		if (sortDirection == null) {
			return ManageReports.ASC;
		}

		return sortDirection;
	}

}
