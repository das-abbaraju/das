package com.picsauditing.util.pagination;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaginationTest {

	Pagination pagination;

	@Mock PaginationDAO paginationDao;

	int pageSize = 10;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pagination = new Pagination(paginationDao, pageSize);
	}

	@Test
	public void testGetTotalPages_OneLessThanMultipleOfPageSize() {
		int multiple = 1;
		when(paginationDao.getPaginationResultCount()).thenReturn(multiple * pageSize - 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple, totalPages);
	}

	@Test
	public void testGetTotalPages_EqualMultipleOfPageSize() {
		int multiple = 5;
		when(paginationDao.getPaginationResultCount()).thenReturn(multiple * pageSize);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple, totalPages);
	}

	@Test
	public void testGetTotalPages_OneMoreThanMultipleOfPageSize() {
		int multiple = 3;
		when(paginationDao.getPaginationResultCount()).thenReturn(multiple * pageSize + 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple + 1, totalPages);
	}

	@Test
	public void testHasFirstPage_FalseIfOnFirstPage() {
		pagination.getResults(1);

		assertFalse(pagination.hasFirstPage());
	}

	@Test
	public void testHasFirstPage_TrueIfNotOnFirstPage() {
		pagination.getResults(2);

		assertTrue(pagination.hasFirstPage());
	}

	@Test
	public void testHasLastPage_FalseIfOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationResultCount()).thenReturn(totalPages * pageSize);
		pagination.getResults(totalPages);

		assertFalse(pagination.hasLastPage());
	}

	@Test
	public void testHasLastPage_TrueIfNotOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationResultCount()).thenReturn(totalPages * pageSize);
		pagination.getResults(totalPages - 1);

		assertTrue(pagination.hasLastPage());
	}

	@Test
	public void testHasPreviousPage_FalseIfOnFirstPage() {
		pagination.getResults(1);

		assertFalse(pagination.hasPreviousPage());
	}

	@Test
	public void testHasPreviousPage_TrueIfNotOnFirstPage() {
		pagination.getResults(2);

		assertTrue(pagination.hasPreviousPage());
	}

	@Test
	public void testHasNextPage_FalseIfOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationResultCount()).thenReturn(totalPages * pageSize);
		pagination.getResults(totalPages);

		assertFalse(pagination.hasNextPage());
	}

	@Test
	public void testHasNextPage_TrueIfNotOnLastPage() {
		int totalPages = 10;
		when(paginationDao.getPaginationResultCount()).thenReturn(totalPages * pageSize);
		pagination.getResults(totalPages - 1);

		assertTrue(pagination.hasNextPage());
	}

	@Test
	public void testHasPagination_FalseIfLessThanPageSizeResults() {
		when(paginationDao.getPaginationResultCount()).thenReturn(pageSize - 1);
		pagination.getResults(1);

		assertFalse(pagination.hasPagination());
	}

	@Test
	public void testHasPagination_FalseIfExactlyPageSizeResults() {
		when(paginationDao.getPaginationResultCount()).thenReturn(pageSize);
		pagination.getResults(1);

		assertFalse(pagination.hasPagination());
	}

	@Test
	public void testHasPagination_TrueIfMoreThanPageSizeResults() {
		when(paginationDao.getPaginationResultCount()).thenReturn(pageSize + 1);
		pagination.getResults(1);

		assertTrue(pagination.hasPagination());
	}

	@Test
	public void testOffset_ZeroForFirstPage() {
		pagination.getResults(1);

		verify(paginationDao).getPaginationResults(0, pageSize);
	}

	@Test
	public void testOffset_OneLessThanPageNumberTimesPageSize() {
		int page = 5;

		pagination.getResults(page);

		verify(paginationDao).getPaginationResults((page - 1) * pageSize, pageSize);
	}

	@Test
	public void testGetPages_OnFirstPageAndMoreThanMaxNavPagesPages() {
		int totalPages = Pagination.MAX_NAV_PAGES + 1;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(1, pages); // 1 2 3 4 5
	}

	@Test
	public void testGetPages_OnlyOnePage() {
		int totalPages = 1;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertEquals(1, pages.get(0).intValue()); // 1
	}

	@Test
	public void testGetPages_TwoPagesOnFirst() {
		int totalPages = 2;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2
	}


	@Test
	public void testGetPages_TwoPagesOnLast() {
		int totalPages = 2;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2
	}

	@Test
	public void testGetPages_ThreePagesOnFirst() {
		int totalPages = 3;
		int page = 1;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetPages_ThreePagesOnSecond() {
		int totalPages = 3;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetPages_ThreePagesOnLast() {
		int totalPages = 3;
		int page = 3;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3
	}

	@Test
	public void testGetPages_FourPagesOnSecond() {
		int totalPages = 4;
		int page = 2;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(totalPages, pages.size());
		assertRange(1, pages); // 1 2 3 4
	}

	@Test
	public void testGetPages_SixPagesOnThird() {
		int totalPages = 6;
		int page = 3;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(1, pages); // 1 2 3 4 5
	}

	@Test
	public void testGetPages_SixPagesOnFourth() {
		int totalPages = 6;
		int page = 4;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(2, pages); // 2 3 4 5 6
	}

	@Test
	public void testGetPages_One() {
		int totalPages = 10;
		int page = 5;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(3, pages); // 3 4 5 6 7
	}

	@Test
	public void testGetPages_Two() {
		int totalPages = 10;
		int page = 9;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(6, pages); // 6 7 8 9 10
	}

	@Test
	public void testGetPages_Three() {
		int totalPages = 25;
		int page = 25;
		List<Integer> pages = setupAndGetPages(totalPages, page);

		assertEquals(Pagination.MAX_NAV_PAGES, pages.size());
		assertRange(21, pages); // 21 22 23 24 25
	}







	private List<Integer> setupAndGetPages(int totalPages, int page) {
		when(paginationDao.getPaginationResultCount()).thenReturn(totalPages * pageSize);
		pagination.getResults(page);
		return pagination.getPages();
	}

	private void assertRange(int start, List<Integer> pages) {
		int num = start;
		for (Integer page : pages) {
			assertEquals(num, page.intValue());
			num += 1;
		}
	}
}
