package com.picsauditing.util.pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * To add pagination to a page:
 *
 * - Have an object (possibly a DAO) implement Paginatable
 * - Create a new object that implements PaginationParameters with all the data
 *   you need for your Paginatable object.
 * - Add a Pagination object to your controller (action class), as well as a getter/setter.
 * - Call it like this: ActionClass.action?pagination.parameters.page=N&pagination.parameters.pageSize=M
 */
public class Pagination<E> {

	private Paginatable<E> dataProvider;
	private PaginationParameters parameters;

	public static final int MAX_NAV_PAGES = 5;

	public void Initialize(PaginationParameters additionalParameters, Paginatable<E> dataProvider) {
		// Copy autowired parameters from URL request
		additionalParameters.setPage(parameters.getPage());
		additionalParameters.setPageSize(parameters.getPageSize());
		this.parameters = additionalParameters;

		this.dataProvider = dataProvider;
	}

	public List<E> getResults() {
		return dataProvider.getPaginationResults(parameters);
	}

	public int getOverallCount() {
		return dataProvider.getPaginationOverallCount(parameters);
	}

	// Page numbers for navigation
	public List<Integer> getNavPages() {
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

	public int getPage() {
		return parameters.getPage();
	}

	public int getPageSize() {
		return parameters.getPageSize();
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

	public int getTotalPages() {
		int totalResults = dataProvider.getPaginationOverallCount(parameters);
		int totalPages = totalResults / parameters.getPageSize();

		if (totalResults % parameters.getPageSize() != 0) {
			totalPages += 1;
		}

		return totalPages;
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

	public PaginationParameters getParameters() {
		return parameters;
	}

	public void setParameters(PaginationParameters parameters) {
		this.parameters = parameters;
	}
}
