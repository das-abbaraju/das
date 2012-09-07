package com.picsauditing.util.pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * To add pagination to a page:
 * 
 * - Have a DAO (possibly a newly created one) implement PaginationDao
 * - Create a new object that implements PaginationParameters with all the data
 *   you need for your DAO.
 * - Add a Pagination object to your controller (action class), as well as a getter/setter.
 */
public final class Pagination<E> {

	private PaginationDAO<E> dao;
	private PaginationParameters parameters;

	public static final int MAX_NAV_PAGES = 5;

	public void Initialize(PaginationParameters parameters, PaginationDAO<E> dao) {
		this.dao = dao;
		this.parameters = parameters;
	}

	public List<E> getResults() {
		return dao.getPaginationResults(parameters);
	}

	// Page numbers for navigation
	public List<Integer> getPages() {
		List<Integer> navPages = new ArrayList<Integer>();

		// MAX_NAV_PAGES total pages, or fewer if fewer pages
		int start = parameters.getPage() - 2;
		if (start < 1) {
			start = 1;
		}

		int totalPages = getTotalPages();

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
		return parameters.getPage() - 1;
	}

	public int getNextPage() {
		return parameters.getPage() + 1;
	}

	public int getLastPage() {
		return getTotalPages();
	}

	// TODO change this to only show if the first page is not in the nav
	public boolean hasFirstPage() {
		if (parameters.getPage() != 1)
			return true;

		return false;
	}

	public boolean hasPreviousPage() {
		if (parameters.getPage() != 1)
			return true;

		return false;
	}

	public boolean hasNextPage() {
		if (parameters.getPage() != getTotalPages())
			return true;

		return false;
	}

	// TODO change this to only show if the last page is not in the nav
	public boolean hasLastPage() {
		if (parameters.getPage() != getTotalPages())
			return true;

		return false;
	}

	public boolean hasPagination() {
		if (getTotalPages() == 1)
			return false;

		return true;
	}

	public int getTotalPages() {
		int totalResults = dao.getPaginationResultCount(parameters);
		int totalPages = totalResults / parameters.getPageSize();

		if (totalResults % parameters.getPageSize() != 0) {
			totalPages += 1;
		}

		return totalPages;
	}
}
