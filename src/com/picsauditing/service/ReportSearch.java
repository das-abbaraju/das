package com.picsauditing.service;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ManageReports;

public final class ReportSearch {

	private Permissions permissions;
	private String sortType;
	private String sortDirection;
	private boolean includeHidden;

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

	public final boolean isIncludeHidden() {
		return includeHidden;
	}

	public static class Builder {

		private Permissions permissions;
		private String sortType;
		private String sortDirection;
		private boolean includeHidden;

		public Builder permissions(Permissions permissions) {
			this.permissions = permissions;
			return this;
		}

		public Builder sortType(String sortType) {
			this.sortType = sortType;
			return this;
		}

		public Builder sortDirection(String sortDirection) {
			this.sortDirection = sortDirection;
			return this;
		}

		public Builder includeHidden(boolean includeHidden) {
			this.includeHidden = includeHidden;
			return this;
		}

		public ReportSearch build() {
			ReportSearch reportSearch = new ReportSearch();
			reportSearch.permissions = this.permissions;
			reportSearch.sortType = this.sortType;
			reportSearch.sortDirection = this.sortDirection;
			reportSearch.includeHidden = this.includeHidden;

			return reportSearch;
		}

	}

}
