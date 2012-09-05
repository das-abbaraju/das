package com.picsauditing.util.pagination;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.BaseTable;

public class Pagination {

	private int page;
	private int pageSize;
	private int totalPages = -1;
	private PaginationDAO dao;

	public static final int MAX_NAV_PAGES = 5;

	public Pagination(PaginationDAO dao, int pageSize) {
		this.dao = dao;
		this.pageSize = pageSize;
	}

	// Must be called first
	public List<? extends BaseTable> getResults(int page) {
		this.page = page;
		int offset = (page - 1) * pageSize;
		return dao.getPaginationResults(offset, pageSize);
	}

	public int getTotalPages() {
		int totalResults = dao.getPaginationResultCount();
		totalPages = totalResults / pageSize;
		if (totalResults % pageSize != 0) {
			totalPages += 1;
		}

		return totalPages;
	}

	// Page numbers for navigation
	public List<Integer> getPages() {
		ensureTotalPagesIsSet();

		List<Integer> navPages = new ArrayList<Integer>();

		// MAX_NAV_PAGES total pages, or fewer if fewer pages
		int start = page - 2;
		if (start < 1) {
			start = 1;
		}

		// Check if we're close to the end
		while ((start + MAX_NAV_PAGES - 1 > totalPages) && (start > 1)) {
			start -= 1;
		}

		for (int i = start; i < start + MAX_NAV_PAGES; i++) {
			if (i <= totalPages) {
				navPages.add(i);
			}
		}

		return navPages;
	}

	public int getFirstPage() {
		return 1;
	}

	public int getPreviousPage() {
		return page - 1;
	}

	public int getNextPage() {
		return page + 1;
	}

	public int getLastPage() {
		ensureTotalPagesIsSet();

		return totalPages;
	}

	// TODO change this to only show if the first page is not in the nav
	public boolean hasFirstPage() {
		if (page != 1)
			return true;

		return false;
	}

	public boolean hasPreviousPage() {
		if (page != 1)
			return true;

		return false;
	}

	public boolean hasNextPage() {
		ensureTotalPagesIsSet();

		if (page != totalPages)
			return true;

		return false;
	}

	// TODO change this to only show if the last page is not in the nav
	public boolean hasLastPage() {
		ensureTotalPagesIsSet();

		if (page != totalPages)
			return true;

		return false;
	}

	public boolean hasPagination() {
		ensureTotalPagesIsSet();

		if (totalPages == 1)
			return false;

		return true;
	}

	private void ensureTotalPagesIsSet() {
		if (totalPages == -1) {
			getTotalPages();
		}
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}
}
