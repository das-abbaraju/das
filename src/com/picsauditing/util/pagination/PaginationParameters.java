package com.picsauditing.util.pagination;

// Subclass this and add whatever parameters you need to send to your DAO to get results.
public class PaginationParameters {

	private int page;
	private int pageSize = 10;

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
