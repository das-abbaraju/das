package com.picsauditing.util.pagination;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaginationTest {

	Pagination pagination;

	@Mock PaginationDAO paginationDao;

	int limit = 10;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		pagination = new Pagination(paginationDao, limit);
	}

	@Test
	public void testGetTotalPages_LessThanLimit() {
		when(paginationDao.getPaginationResultCount()).thenReturn(limit - 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(1, totalPages);
	}

	@Test
	public void testGetTotalPages_EqualMutlipleOfLimit() {
		int multiple = 5;
		when(paginationDao.getPaginationResultCount()).thenReturn(multiple * limit);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple, totalPages);
	}

	@Test
	public void testGetTotalPages_OneMoreThanEqualMultipleOfLimit() {
		int multiple = 3;
		when(paginationDao.getPaginationResultCount()).thenReturn(multiple * limit + 1);

		int totalPages = pagination.getTotalPages();

		assertEquals(multiple + 1, totalPages);
	}
}
