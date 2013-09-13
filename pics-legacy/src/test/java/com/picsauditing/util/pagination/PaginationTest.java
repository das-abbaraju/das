package com.picsauditing.util.pagination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.Report;

public class PaginationTest {

	Pagination<Report> pagination;

	@Mock Paginatable<Report> paginationDao;
	@Mock PaginationParameters parameters;

	private final int pageSize = 5;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pagination = new Pagination<Report>();

		// This step is done automatically by Struts through URL parameters
		parameters.setPage(1);
		parameters.setPageSize(pageSize);
		pagination.setParameters(parameters);

		// This is what your code will call
		pagination.initialize(parameters, paginationDao);
	}

	@Test
	public void testGetTotalPages_OneLessThanMultipleOfPageSize() {
		int multiple = 1;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(multiple * pageSize - 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple, totalPages);
	}

	@Test
	public void testGetTotalPages_EqualMultipleOfPageSize() {
		int multiple = 5;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(multiple * pageSize);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple, totalPages);
	}

	@Test
	public void testGetTotalPages_OneMoreThanMultipleOfPageSize() {
		int multiple = 3;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(multiple * pageSize + 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple + 1, totalPages);
	}

	@Test
	public void testHasFirstPage_FalseIfOnFirstPage() {
		parameters.setPage(1);
		pagination.getResults();

		assertFalse(pagination.hasFirstPage());
	}

	@Test
	public void testHasFirstPage_TrueIfNotOnFirstPage() {
		parameters.setPage(2);
		pagination.getResults();

		assertTrue(pagination.hasFirstPage());
	}

	@Test
	public void testHasLastPage_FalseIfOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(totalPages * pageSize);
		parameters.setPage(totalPages);
		pagination.getResults();

		assertFalse(pagination.hasLastPage());
	}

	@Test
	public void testHasLastPage_TrueIfNotOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(totalPages * pageSize);
		parameters.setPage(totalPages - 1);
		pagination.getResults();

		assertTrue(pagination.hasLastPage());
	}

	@Test
	public void testHasPreviousPage_FalseIfOnFirstPage() {
		parameters.setPage(1);
		pagination.getResults();

		assertFalse(pagination.hasPreviousPage());
	}

	@Test
	public void testHasPreviousPage_TrueIfNotOnFirstPage() {
		parameters.setPage(2);
		pagination.getResults();

		assertTrue(pagination.hasPreviousPage());
	}

	@Test
	public void testHasNextPage_FalseIfOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(totalPages * pageSize);
		parameters.setPage(totalPages);
		pagination.getResults();

		assertFalse(pagination.hasNextPage());
	}

	@Test
	public void testHasNextPage_TrueIfNotOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(totalPages * pageSize);
		parameters.setPage(totalPages - 1);
		pagination.getResults();

		assertTrue(pagination.hasNextPage());
	}

	@Test
	public void testHasPagination_FalseIfLessThanPageSizeResults() {
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(pageSize - 1);
		parameters.setPage(1);
		pagination.getResults();

		assertFalse(pagination.hasPagination());
	}

	@Test
	public void testHasPagination_FalseIfExactlyPageSizeResults() {
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(pageSize);
		parameters.setPage(1);
		pagination.getResults();

		assertFalse(pagination.hasPagination());
	}

	@Test
	public void testHasPagination_TrueIfMoreThanPageSizeResults() {
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(pageSize + 1);
		parameters.setPage(1);
		pagination.getResults();

		assertTrue(pagination.hasPagination());
	}

	@Test
	public void testGetNavPages_OnFirstPageAndMoreThanMaxNavPagesPages() {
		int totalPages = Pagination.MAX_NAV_PAGES + 1;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(1, pages); // 1 2 3 4 5
	}

	@Test
	public void testGetNavPages_OnlyOnePage() {
		int totalPages = 1;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1
	}

	@Test
	public void testGetNavPages_TwoPagesOnFirst() {
		int totalPages = 2;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2
	}

	@Test
	public void testGetNavPages_TwoPagesOnLast() {
		int totalPages = 2;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2
	}

	@Test
	public void testGetNavPages_ThreePagesOnFirst() {
		int totalPages = 3;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetNavPages_ThreePagesOnSecond() {
		int totalPages = 3;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetNavPages_ThreePagesOnLast() {
		int totalPages = 3;
		int page = 3;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetNavPages_FourPagesOnFirst() {
		int totalPages = 4;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3 4
	}

	@Test
	public void testGetNavPages_FourPagesOnSecond() {
		int totalPages = 4;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3 4
	}

	@Test
	public void testGetNavPages_FourPagesOnThird() {
		int totalPages = 4;
		int page = 3;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3 4
	}

	@Test
	public void testGetNavPages_FourPagesOnLast() {
		int totalPages = 4;
		int page = 4;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3 4
	}

	@Test
	public void testGetNavPages_SixPagesOnThird() {
		int totalPages = 6;
		int page = 3;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(1, pages); // 1 2 3 4 5
	}

	@Test
	public void testGetNavPages_SixPagesOnFourth() {
		int totalPages = 6;
		int page = 4;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(2, pages); // 2 3 4 5 6
	}

	@Test
	public void testGetNavPages_RandomOne() {
		int totalPages = 10;
		int page = 5;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(3, pages); // 3 4 5 6 7
	}

	@Test
	public void testGetNavPages_RandomTwo() {
		int totalPages = 10;
		int page = 9;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(6, pages); // 6 7 8 9 10
	}

	@Test
	public void testGetNavPages_RandomThree() {
		int totalPages = 25;
		int page = 25;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(21, pages); // 21 22 23 24 25
	}

	@Test
	public void testGetNavPages_RandomFour() {
		int totalPages = 25;
		int page = 22;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(20, pages); // 20 21 22 23 24
	}

	private List<Integer> setupAndGetPages(int totalPages, int page) {
		when(paginationDao.getPaginationOverallCount(parameters)).thenReturn(totalPages * pageSize);
		parameters.setPage(page);
		pagination.getResults();
		return pagination.getNavPages();
	}

	private void assertRange(int start, List<Integer> pages) {
		int num = start;
		for (Integer page : pages) {
			assertEquals(num, page.intValue());
			num += 1;
		}
	}
}
