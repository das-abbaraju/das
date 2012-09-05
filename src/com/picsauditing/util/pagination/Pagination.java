package com.picsauditing.util.pagination;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.BaseTable;

public class Pagination {

	private int page;
	private int pageSize;
	private int totalPages;
	private PaginationDAO dao;

	public Pagination(PaginationDAO dao, int pageSize) {
		this.dao = dao;
		this.pageSize = pageSize;
	}

	// Must be called first
	public List<? extends BaseTable> getResults(int page) {
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

	public List<Integer> getPages() {
		List<Integer> navPages = new ArrayList<Integer>();

		// 5 total pages, or fewer if fewer pages
		int start = page - 2;
		if (start < 1) {
			start = 1;
		}

		for (int i = start; i < page + 2; i++) {
			if (i < totalPages) {
				navPages.add(i);
			}
		}

		return navPages;
	}

	// TODO change this to only show if the first page is not in the nav
	public boolean hasFirstPage() {
		if (page != 1)
			return true;

		return false;
	}

	public int getFirstPage() {
		return 1;
	}

	public boolean hasPreviousPage() {
		if (page != 1)
			return true;

		return false;
	}

	public int getPreviousPage() {
		return page - 1;
	}
	
	public boolean hasNextPage() {
		if (page != totalPages)
			return true;
		
		return false;
	}

	public int getNextPage() {
		return page + 1;
	}

	// TODO change this to only show if the last page is not in the nav
	public boolean hasLastPage() {
		if (page != totalPages)
			return true;
		
		return false;
	}

	public int getLastPage() {
		return totalPages;
	}

	public boolean hasPagination() {
		if (totalPages == 1)
			return true;

		return false;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}
}
