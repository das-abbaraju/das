package com.picsauditing.util.pagination;

import com.picsauditing.util.DynamicReportConstants;

/**
 *  Subclass this and add whatever parameters you need to send to your Paginatable class to get results.
 */
public class PaginationParameters {

	private int page = DynamicReportConstants.DEFAULT_PAGE_NUMBER;
	private int pageSize = DynamicReportConstants.DEFAULT_RESULTS_PER_PAGE;

	public final int getPage() {
		return page;
	}

	public final void setPage(int page) {
		this.page = page;
	}

	public final int getPageSize() {
		return pageSize;
	}

	public final void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
